package com.paddi.service.impl;

import cn.hutool.core.lang.UUID;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.paddi.common.FrameType;
import com.paddi.common.GolangGameStatus;
import com.paddi.common.RedisKey;
import com.paddi.common.SearchUserStatusEnum;
import com.paddi.entity.GolangBattle;
import com.paddi.entity.User;
import com.paddi.entity.vo.UserVo;
import com.paddi.exception.BadRequestException;
import com.paddi.exception.RequestParamValidationException;
import com.paddi.message.Frame;
import com.paddi.netty.UserChannelManager;
import com.paddi.service.GolangService;
import com.paddi.service.UserService;
import com.paddi.utils.mapstruct.UserMapStruct;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年12月08日 00:18:31
 */
@Service
@Slf4j
public class GolangServiceImpl implements GolangService {

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private UserService userService;

    @Override
    public GolangBattle create(Long userId) {
        User user = userService.getById(userId);
        if(user == null) {
            throw new RequestParamValidationException(ImmutableMap.of("userId", userId, "cause", "用户不存在"));
        }
        if(!isOnline(userId)) {
            throw new BadRequestException(ImmutableMap.of("cause", "用户已经掉线,无法创建游戏对局,请连接网络后重试!"));
        }
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
        Integer status = (Integer) userService.preConditionSearchUser(userId, friendId).get("status");
        if(!status.equals(SearchUserStatusEnum.ALREADY_FRIENDS.status)) {
            throw new BadRequestException(ImmutableMap.of("cause", "该用户和你不是好友关系,无权限邀请其进入游戏"));
        }
        if(!isOnline(userId) || !isOnline(friendId)) {
            throw new BadRequestException(ImmutableMap.of("cause", "用户或对方已经掉线,无法邀请进入游戏对局,请连接网络后重试!"));
        }
        //已经创建房间才可以邀请
        if(!redisTemplate.opsForSet().isMember(RedisKey.BATTLE_SET_KEY, userId)) {
            throw new BadRequestException(ImmutableMap.of("cause", "未创建游戏房间,无法邀请好友"));
        }
        //对方已经在其他游戏对局中
        if(redisTemplate.opsForSet().isMember(RedisKey.BATTLE_SET_KEY, friendId)) {
            throw new BadRequestException(ImmutableMap.of("cause", "对方正在进行游戏对局"));
        }
        User user = userService.getById(userId);
        if(user == null) {
            throw new RequestParamValidationException(ImmutableMap.of("userId", userId, "cause", "用户不存在"));
        }
        //通过WebSocket向好友推送游戏请求
        Channel friendChannel = UserChannelManager.getChannel(friendId);
        Frame invitationFrame = Frame.builder()
                           .type(FrameType.BATTLE_INVITATION.getType()).content("您的好友"+user.getUsername()+"向您发起游戏邀请")
                           .extend(ImmutableMap.of("user", UserMapStruct.USER_MAPPING.userToUserVo(user)))
                           .build();
        friendChannel.writeAndFlush(new TextWebSocketFrame(new Gson().toJson(invitationFrame)));
    }

    @Override
    public Map<String, Object> check(Long userId) {
        HashMap<String, Object> checkResult = new HashMap<>(2);
        Boolean havePlayRoom = redisTemplate.opsForSet().isMember(RedisKey.BATTLE_SET_KEY, userId);
        checkResult.put("havePlayRoom", havePlayRoom);
        if(havePlayRoom) {
            GolangBattle golangBattle = (GolangBattle) redisTemplate.opsForHash().get(RedisKey.BATTLE_HASH_KEY, RedisKey.BATTLE_FILED_KEY + userId);
            checkResult.put("playRoomInfo", golangBattle);
        }
        return checkResult;
    }

    private Boolean isOnline(Long userId) {
        return UserChannelManager.contains(userId);
    }
}
