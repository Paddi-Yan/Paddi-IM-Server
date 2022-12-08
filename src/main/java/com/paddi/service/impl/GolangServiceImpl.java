package com.paddi.service.impl;

import cn.hutool.core.lang.UUID;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.paddi.common.*;
import com.paddi.entity.GolangBattle;
import com.paddi.entity.User;
import com.paddi.entity.vo.PlayChessRequestVo;
import com.paddi.entity.vo.UserVo;
import com.paddi.exception.BadRequestException;
import com.paddi.exception.InternalServerException;
import com.paddi.exception.RequestParamValidationException;
import com.paddi.message.Frame;
import com.paddi.netty.UserChannelManager;
import com.paddi.service.GolangService;
import com.paddi.service.UserService;
import com.paddi.utils.GolangUtil;
import com.paddi.utils.mapstruct.UserMapStruct;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年12月08日 00:18:31
 */
@Service
@Slf4j
public class GolangServiceImpl implements GolangService {

    public static final String PLAY_ROOM_INFO = "playRoomInfo";
    public static final String GOLANG_PIECES_TYPE = "golangPiecesType";
    public static final String IS_WIN = "isWin";
    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private UserService userService;

    @Override
    public GolangBattle create(Long userId) {
        User user = checkUserExist(userId);
        checkCurrentUserOnline(userId);
        if(redisTemplate.opsForSet().isMember(RedisKey.BATTLE_SET_KEY, userId)) {
            return (GolangBattle) redisTemplate.opsForHash().get(RedisKey.BATTLE_HASH_KEY, RedisKey.BATTLE_FILED_KEY + userId);
        }
        UserVo userVo = UserMapStruct.USER_MAPPING.userToUserVo(user);
        GolangBattle battle = GolangBattle.builder()
                                         .id(UUID.fastUUID().toString(true))
                                         .createUser(userVo)
                                         .status(GolangGameStatus.WAITING)
                                         .build();
        redisTemplate.opsForSet().add(RedisKey.BATTLE_SET_KEY, userId);
        redisTemplate.opsForHash().put(RedisKey.BATTLE_HASH_KEY, RedisKey.BATTLE_FILED_KEY + userId, battle);
        return battle;
    }

    @Override
    public void invite(Long userId, Long friendId) {
        checkRelationShip(userId, friendId);
        checkAllOnline(userId, friendId);
        //已经创建房间才可以邀请
        checkInRoom(userId);
        //对方已经在其他游戏对局中
        if(redisTemplate.opsForSet().isMember(RedisKey.BATTLE_SET_KEY, friendId)) {
            throw new BadRequestException(ImmutableMap.of("cause", "对方正在进行游戏对局"));
        }
        User user = checkUserExist(userId);
        //通过WebSocket向好友推送游戏请求
        Channel friendChannel = UserChannelManager.getChannel(friendId);
        Frame invitationFrame = Frame.builder()
                           .type(FrameType.BATTLE_INVITATION.getType()).content("您的好友"+user.getUsername()+"向您发起游戏邀请")
                           .extend(ImmutableMap.of("user", UserMapStruct.USER_MAPPING.userToUserVo(user)))
                           .build();
        friendChannel.writeAndFlush(new TextWebSocketFrame(new Gson().toJson(invitationFrame)));
    }

    @NotNull
    private User checkUserExist(Long userId) {
        User user = userService.getById(userId);
        if(user == null) {
            throw new RequestParamValidationException(ImmutableMap.of("userId", userId, "cause", "用户不存在"));
        }
        return user;
    }

    private void checkAllOnline(Long userId, Long friendId) {
        if(!isOnline(userId) || !isOnline(friendId)) {
            throw new BadRequestException(ImmutableMap.of("cause", "用户或对方已经掉线,请稍后重试!"));
        }
    }

    private void checkCurrentUserOnline(Long userId) {
        if(!isOnline(userId)) {
            throw new BadRequestException(ImmutableMap.of("cause", "用户已经掉线,无法完成当前操作,请稍后重试!"));
        }
    }

    private void checkRelationShip(Long userId, Long friendId) {
        Integer status = (Integer) userService.preConditionSearchUser(userId, friendId).get("status");
        if(!status.equals(SearchUserStatusEnum.ALREADY_FRIENDS.status)) {
            throw new BadRequestException(ImmutableMap.of("cause", "该用户和你不是好友关系,无权限邀请其进入游戏"));
        }
    }

    @Override
    public Map<String, Object> check(Long userId) {
        HashMap<String, Object> checkResult = new HashMap<>(2);
        Boolean havePlayRoom = redisTemplate.opsForSet().isMember(RedisKey.BATTLE_SET_KEY, userId);
        checkResult.put("havePlayRoom", havePlayRoom);
        if(havePlayRoom) {
            GolangBattle golangBattle = (GolangBattle) redisTemplate.opsForHash().get(RedisKey.BATTLE_HASH_KEY, RedisKey.BATTLE_FILED_KEY + userId);
            checkResult.put(PLAY_ROOM_INFO, golangBattle);
        }
        return checkResult;
    }

    @Override
    public void handleInvitation(Long userId, Long inviteUserId, Integer handleType) {
        User user = checkUserExist(userId);
        //拒绝邀请
        if(handleType.equals(GolangInvitationHandleType.REFUSE.getCode())) {
            if(UserChannelManager.contains(inviteUserId)) {
                UserChannelManager.getChannel(inviteUserId).writeAndFlush(
                        new Gson().toJson(
                                Frame.builder().content("用户" + user.getUsername() + "拒绝了您的游戏邀请")
                                     .type(FrameType.BATTLE_INVITATION_HANDLE_RESULT.getType())
                                     .build()));
            }
            return;
        }
        User friend = checkUserExist(inviteUserId);
        checkRelationShip(userId, inviteUserId);
        checkAllOnline(userId, inviteUserId);
        //检查对局是否存在
        GolangBattle golangBattle = checkInRoom(inviteUserId);
        //检查房间是否是等待邀请状态
        if(!golangBattle.getStatus().equals(GolangGameStatus.WAITING)) {
            throw new BadRequestException(ImmutableMap.of("cause", "该房间当前状态已经不可以进入"));
        }
        golangBattle.setStatus(GolangGameStatus.ALL_IN);
        //被邀请的用户是当前处理邀请的用户User
        golangBattle.setInvitedUser(UserMapStruct.USER_MAPPING.userToUserVo(user));
        //将当前用户加入游戏房间
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                redisOperations.multi();
                redisOperations.opsForSet().add(RedisKey.BATTLE_SET_KEY, userId);
                redisOperations.opsForHash().put(RedisKey.BATTLE_HASH_KEY, RedisKey.BATTLE_FILED_KEY + inviteUserId, golangBattle);
                redisOperations.opsForHash().put(RedisKey.BATTLE_HASH_KEY, RedisKey.BATTLE_FILED_KEY + userId, golangBattle);
                return redisOperations.exec();
            }
        });
        log.info("当前游戏房间信息: {}", golangBattle);
        //推送已经接收邀请的消息的给好友
        Channel userChannel = UserChannelManager.getChannel(userId);
        Channel friendChannel = UserChannelManager.getChannel(inviteUserId);
        Frame playRoomInfo = Frame.builder()
                           .content("游戏房间状态信息更新")
                           .type(FrameType.BATTLE_STATUS_UPDATE.getType())
                           .extend(ImmutableMap.of(PLAY_ROOM_INFO, golangBattle))
                           .build();
        TextWebSocketFrame msg = new TextWebSocketFrame(new Gson().toJson(playRoomInfo));
        userChannel.writeAndFlush(msg);
        friendChannel.writeAndFlush(msg);
        userChannel.writeAndFlush(new TextWebSocketFrame(new Gson().toJson(
                Frame.builder().content("用户" + user.getUsername() + "接受了您的游戏邀请")
                     .type(FrameType.BATTLE_INVITATION_HANDLE_RESULT.getType())
                     .build())));
    }

    @Override
    public void toBeReady(String id, Long userId) {
        checkUserExist(userId);
        GolangBattle golangBattle = checkInRoom(userId);
        //双方都进入房间才可以就绪
        if(!golangBattle.getStatus().equals(GolangGameStatus.ALL_IN)) {
            throw new BadRequestException(ImmutableMap.of("cause", "还有一方未进入游戏房间,无法进行就绪"));
        }
        UserVo createUser = golangBattle.getCreateUser();
        UserVo invitedUser = golangBattle.getInvitedUser();
        if(createUser == null || invitedUser == null) {
            throw new BadRequestException(ImmutableMap.of("cause", "获取游戏房间用户信息失败"));
        }
        //获取对方的用户ID
        Long friendId = createUser.getId().equals(userId) ? invitedUser.getId() : createUser.getId();
        //如果有一方已经就绪了 && 就绪用户列表不得为空
        if(golangBattle.getStatus().equals(GolangGameStatus.SOMEONE_ALREADY) && !golangBattle.getReadyUserList().isEmpty()) {
            //开始游戏
            List<Long> readyUserList = golangBattle.getReadyUserList();
            readyUserList.add(userId);
            golangBattle.setReadyUserList(readyUserList);
            golangBattle.setStatus(GolangGameStatus.START);
            //TODO 初始化棋盘矩阵
            Integer size = GolangBattle.SIZE;
            golangBattle.setSquare(new int[size][size]);
            //分配黑白棋
            boolean random = new Random().nextBoolean();
            //如果为true user->黑棋 friend->白棋
            if(random) {
                golangBattle.setBlackPiecesUserId(userId);
                golangBattle.setWhitePiecesUserId(friendId);
            }else {
                golangBattle.setBlackPiecesUserId(friendId);
                golangBattle.setWhitePiecesUserId(userId);
            }
            Long blackPiecesUserId = golangBattle.getBlackPiecesUserId();
            Long whitePiecesUserId = golangBattle.getWhitePiecesUserId();
            //检查双方是否同时在线
            //如果有一方不在线或者双方都不在线不会开始游戏
            checkAllOnline(blackPiecesUserId, whitePiecesUserId);
            //更新信息
            updateGolangBattle(userId, friendId, golangBattle);
            //为双方推送游戏信息更新
            Frame gameStartInfo = Frame.builder().content("游戏开始").type(FrameType.BATTLE_START.getType()).build();
            Gson gson = new Gson();
            HashMap<String, Object> extend = new HashMap<>(2);
            extend.put(PLAY_ROOM_INFO, golangBattle);
            //黑棋方
            Channel blackPiecesUserChannel = UserChannelManager.getChannel(blackPiecesUserId);
            extend.put(GOLANG_PIECES_TYPE, GolangPiecesType.BLACK_PIECES.getCode());
            gameStartInfo.setExtend(extend);
            blackPiecesUserChannel.writeAndFlush(gson.toJson(gameStartInfo));
            //白棋方
            Channel whitePiecesUserChannel = UserChannelManager.getChannel(whitePiecesUserId);
            extend.put(GOLANG_PIECES_TYPE, GolangPiecesType.WHITE_PIECES.getCode());
            gameStartInfo.setExtend(extend);
            whitePiecesUserChannel.writeAndFlush(gson.toJson(gameStartInfo));
            //TODO 同时通知黑棋方先下棋
            blackPiecesUserChannel.writeAndFlush(new TextWebSocketFrame(
                    gson.toJson(Frame.builder().content("Now is your turn")
                                     .type(FrameType.GOLANG_IS_YOUR_TURN.getType())
                                     .build())));
        }else {
            //检查就绪的用户是否在线
            checkCurrentUserOnline(userId);
            //更改游戏房间状态
            golangBattle.setStatus(GolangGameStatus.SOMEONE_ALREADY);
            //更新准备就绪的用户列表
            golangBattle.setReadyUserList(ImmutableList.of(userId));
            updateGolangBattle(userId, friendId, golangBattle);
            //推送游戏房间状态更新信息
            Frame playRoomInfo = Frame.builder()
                                            .content("有一方已经准备就绪")
                                            .type(FrameType.BATTLE_STATUS_UPDATE.getType())
                                            .extend(ImmutableMap.of(PLAY_ROOM_INFO, golangBattle))
                                            .build();
            Channel userChannel = UserChannelManager.getChannel(userId);
            Channel friendChannel = UserChannelManager.getChannel(friendId);
            TextWebSocketFrame msg = new TextWebSocketFrame(new Gson().toJson(playRoomInfo));
            userChannel.writeAndFlush(msg);
            if(friendChannel != null) {
                friendChannel.writeAndFlush(msg);
            }
        }
    }

    @NotNull
    private GolangBattle checkInRoom(Long userId) {
        Boolean isInRoom = redisTemplate.opsForSet().isMember(RedisKey.BATTLE_SET_KEY, userId);
        GolangBattle golangBattle = (GolangBattle) redisTemplate.opsForHash().get(RedisKey.BATTLE_HASH_KEY, RedisKey.BATTLE_FILED_KEY + userId);
        if(!isInRoom || golangBattle == null) {
            throw new BadRequestException(ImmutableMap.of("cause", "用户没有在游戏房间中"));
        }
        return golangBattle;
    }

    @Override
    public void playChess(PlayChessRequestVo playChessRequestVo) {
        Long userId = playChessRequestVo.getUserId();
        Integer m = playChessRequestVo.getM();
        Integer n = playChessRequestVo.getN();
        Integer golangPiecesType = playChessRequestVo.getGolangPiecesType();
        checkUserExist(userId);
        GolangBattle golangBattle = checkInRoom(userId);
        UserVo createUser = golangBattle.getCreateUser();
        UserVo invitedUser = golangBattle.getInvitedUser();
        //获取对方的用户ID
        Long friendId = createUser.getId().equals(userId) ? invitedUser.getId() : createUser.getId();
        //检查x和y是否处于矩阵范围内 && 检查棋子类型 && 检查当前位置是否已经放置了棋子
        int[][] square = checkCoordinate(golangBattle, playChessRequestVo);
        square[m][n] = golangPiecesType;
        golangBattle.setSquare(square);
        //推送棋盘更新信息
        Frame golangUpdateInfo = Frame.builder()
                                  .content("棋盘信息更新")
                                  .type(FrameType.GOLANG_SQUARE_UPDATE.getType())
                                  .extend(ImmutableMap.of(PLAY_ROOM_INFO, golangBattle))
                                  .build();
        Gson gson = new Gson();
        TextWebSocketFrame msg = new TextWebSocketFrame(gson.toJson(golangUpdateInfo));
        if(UserChannelManager.contains(userId)) {
            UserChannelManager.getChannel(userId).writeAndFlush(msg);
        }
        if(UserChannelManager.contains(friendId)) {
            UserChannelManager.getChannel(friendId).writeAndFlush(msg);
        }
        //某一方已经形成五连子
        if(checkGolangEnd(square, m, n, golangPiecesType)) {
            //结束游戏
            golangBattle.setStatus(GolangGameStatus.END);
            Frame gameOverInfo = Frame.builder().content("游戏结束").type(FrameType.BATTLE_END.getType()).build();
            if(UserChannelManager.contains(userId)) {
                gameOverInfo.setExtend(ImmutableMap.of(IS_WIN, true));
                UserChannelManager.getChannel(userId).writeAndFlush(new TextWebSocketFrame(gson.toJson(gameOverInfo)));
            }
            if(UserChannelManager.contains(friendId)) {
                gameOverInfo.setExtend(ImmutableMap.of(IS_WIN, false));
                UserChannelManager.getChannel(userId).writeAndFlush(new TextWebSocketFrame(gson.toJson(gameOverInfo)));
            }
            try {
                Thread.sleep(30000);
                //30s后删除缓存中的棋盘信息
                deleteGolangInfo(userId, friendId);
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
        }else  {
            //继续进行
            //通知另一方下棋
            if(UserChannelManager.contains(friendId)) {
                UserChannelManager.getChannel(friendId).writeAndFlush(
                        new TextWebSocketFrame(gson.toJson(
                                Frame.builder().content("Now is your turn")
                                         .type(FrameType.GOLANG_IS_YOUR_TURN.getType())
                                         .build())));
            }
        }
    }

    @Override
    public void toBeEnd(Long userId) {
        GolangBattle golangBattle = checkInRoom(userId);
        UserVo createUser = golangBattle.getCreateUser();
        UserVo invitedUser = golangBattle.getInvitedUser();
        Long friendId = createUser.getId().equals(userId) ? invitedUser.getId() : createUser.getId();
        deleteGolangInfo(userId, friendId);
    }

    private void deleteGolangInfo(Long userId, Long friendId) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                redisOperations.multi();
                redisOperations.opsForSet().remove(RedisKey.BATTLE_SET_KEY, userId);
                redisOperations.opsForSet().remove(RedisKey.BATTLE_SET_KEY, friendId);
                redisOperations.opsForHash().delete(RedisKey.BATTLE_HASH_KEY, RedisKey.BATTLE_FILED_KEY + userId);
                redisOperations.opsForHash().delete(RedisKey.BATTLE_HASH_KEY, RedisKey.BATTLE_FILED_KEY + friendId);
                return redisOperations.exec();
            }
        });
    }

    private boolean checkGolangEnd(int[][] square, Integer m, Integer n, Integer golangPiecesType) {
        return GolangUtil.checkWin(square, m, n, golangPiecesType);
    }

    private int[][] checkCoordinate(GolangBattle golangBattle, PlayChessRequestVo playChessRequestVo) {
        Integer m = playChessRequestVo.getM();
        Integer n = playChessRequestVo.getN();
        Integer golangPiecesType = playChessRequestVo.getGolangPiecesType();
        Integer size = GolangBattle.SIZE;
        if(m < 0 || n < 0 || m > size || n > size) {
            throw new BadRequestException(ImmutableMap.of("cause", "棋子坐标不合法!"));
        }
        if(!golangPiecesType.equals(GolangPiecesType.BLACK_PIECES.getCode()) && !golangPiecesType.equals(GolangPiecesType.WHITE_PIECES.getCode())) {
            throw new BadRequestException(ImmutableMap.of("cause", "棋子类型不合法!"));
        }
        int[][] square = golangBattle.getSquare();
        if(square == null) {
            throw new InternalServerException(ImmutableMap.of("cause","棋盘初始化出现错误,请联系管理员"));
        }
        //0：表示该位置没有下过棋子
        if(square[m][n] != 0) {
            //该位置已经下过棋子了 无法完成该操作
            throw new BadRequestException(ImmutableMap.of("cause", "当前位置[" + m +", " + n + "]已经下过棋子了,无法完成当前操作"));
        }
        return square;
    }

    private void updateGolangBattle(Long userId, Long friendId, GolangBattle golangBattle) {
        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                redisOperations.multi();
                redisOperations.opsForHash().put(RedisKey.BATTLE_HASH_KEY, RedisKey.BATTLE_FILED_KEY + userId, golangBattle);
                redisOperations.opsForHash().put(RedisKey.BATTLE_HASH_KEY, RedisKey.BATTLE_FILED_KEY + friendId, golangBattle);
                return redisOperations.exec();
            }
        });
    }

    private Boolean isOnline(Long userId) {
        return UserChannelManager.contains(userId);
    }
}
