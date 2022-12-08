package com.paddi.service.impl;

import cn.hutool.core.lang.UUID;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.paddi.common.FrameType;
import com.paddi.common.MessageReadEnum;
import com.paddi.common.MessageType;
import com.paddi.common.SearchUserStatusEnum;
import com.paddi.entity.vo.ChatHistoryVo;
import com.paddi.entity.vo.MessageVo;
import com.paddi.exception.*;
import com.paddi.mapper.PrivateChatMapper;
import com.paddi.message.Frame;
import com.paddi.message.PrivateChatMessage;
import com.paddi.netty.UserChannelManager;
import com.paddi.service.ChatService;
import com.paddi.service.UserService;
import com.paddi.utils.MinioUtil;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月27日 15:55:20
 */
@Service
@Transactional(rollbackFor = BaseException.class)
public class ChatServiceImpl implements ChatService {

    @Resource
    private PrivateChatMapper privateChatMapper;

    @Resource
    private UserService userService;

    @Resource
    private MinioUtil minioUtil;

    @Value("${minio.fileBucket}")
    private String fileBucketName;

    @Override
    public PrivateChatMessage sendPrivateMessage(PrivateChatMessage message){
        privateChatMapper.insert(message);
        return message;
    }

    @Override
    public void sendUnreadMessage(Channel channel, Long userId) {
        List<PrivateChatMessage> messageList = privateChatMapper.selectList(new QueryWrapper<PrivateChatMessage>()
                .eq("receiver_id", userId).eq("is_read", MessageReadEnum.UNREAD.getStatus())
                .orderByDesc("send_time"));
        if(!messageList.isEmpty()) {
            /*Map<Long, List<PrivateChatMessage>> messageMap = messageList.stream()
                                                               .collect(Collectors.groupingBy(PrivateChatMessage::getSenderId));*/
            Map<Long, Long> messageMap = messageList.stream()
                                                 .collect(Collectors.groupingBy(PrivateChatMessage :: getSenderId, Collectors.counting()));
            Frame frame = Frame.builder().receiverId(userId).type(FrameType.UNREAD_PRIVATE_MESSAGE.getType())
                               .extend(messageMap).build();
            channel.writeAndFlush(new TextWebSocketFrame(new Gson().toJson(frame)));
        }
    }

    @Override
    public void signMessageAlreadyRead(Long userId, Long friendId) {
        privateChatMapper.signMessageAlreadyRead(userId, friendId);
    }

    @Override
    public List<PrivateChatMessage> getPrivateChatHistory(ChatHistoryVo chatHistoryVo) {
        HashMap<String, Object> checkMap = userService.preConditionSearchUser(chatHistoryVo.getUserId(), chatHistoryVo.getAnotherId());
        Integer status = (Integer) checkMap.get("status");
        //校验是否是好友关系
        if(!status.equals(SearchUserStatusEnum.ALREADY_FRIENDS.status)) {
            throw new AuthenticationException(ImmutableMap.of("userId", chatHistoryVo.getUserId(), "friendId", chatHistoryVo.getAnotherId()));
        }
        Page<PrivateChatMessage> page = new Page<>(chatHistoryVo.getCurrent(), chatHistoryVo.getSize());
        Long userId = chatHistoryVo.getUserId();
        Long friendId = chatHistoryVo.getAnotherId();
        //TODO 标记消息为已读
        signMessageAlreadyRead(userId, friendId);
        List<PrivateChatMessage> privateChatHistoryList = privateChatMapper.getPrivateChatHistory(page, userId, friendId);
        return privateChatHistoryList;
    }

    @Override
    public Boolean messageAlreadySend(String sequenceId) {
        return privateChatMapper.selectById(sequenceId) != null;
    }

    @Override
    public void sendPrivateChatMessage(MessageVo messageVo) {
        //检查用户ID是否合法
        Long senderId = messageVo.getSenderId();
        Long receiverId = messageVo.getReceiverId();
        checkUserExist(senderId, receiverId);
        //检查是否是好友关系
        checkRelationShip(senderId, receiverId);
        //消息体不能为空串
        String content = messageVo.getContent();
        if(StringUtils.isEmpty(content)) {
            throw new RequestParamValidationException(ImmutableMap.of("cause", "消息内容不得为空"));
        }
        //TODO 推送消息和持久化可以异步进行->利用消息队列解耦
        String id = UUID.randomUUID().toString(true);
        //好友在线
        if(UserChannelManager.contains(receiverId)) {
            Frame frame = Frame.builder()
                               .sequenceId(id)
                               .senderId(senderId)
                               .content(content)
                               .receiverId(receiverId)
                               .type(FrameType.PRIVATE_CHAT.getType())
                               .build();
            //推送私信
            UserChannelManager.getChannel(receiverId)
                              .writeAndFlush(new Gson().toJson(frame));
        }
        //持久化消息
        PrivateChatMessage message = PrivateChatMessage.builder()
                                                     .senderId(senderId)
                                                     .content(content)
                                                     .sendTime(LocalDateTime.now())
                                                     .receiverId(receiverId)
                                                     .alreadyRead(MessageReadEnum.UNREAD.getStatus())
                                                     .type(MessageType.TEXT.getCode())
                                                     .build();
        message.setId(id);
        privateChatMapper.insert(message);
    }



    @Override
    public void transferFile(MessageVo messageVo, MultipartFile file) {
        if(file == null || file.isEmpty()) {
            throw new BadRequestException(ImmutableMap.of("cause", "文件为空或损坏,传输文件失败!"));
        }
        Long senderId = messageVo.getSenderId();
        Long receiverId = messageVo.getReceiverId();
        checkUserExist(senderId, receiverId);
        checkRelationShip(senderId, receiverId);
        Map<String, String> transferResult;
        try {
           transferResult = minioUtil.upload(file, fileBucketName);
        } catch(Exception e) {
            throw new InternalServerException(ImmutableMap.of("cause", "文件传输失败,请稍后重试!"));
        }
        String fileName = transferResult.get("fileName");
        String size = transferResult.get("size");
        String id = UUID.randomUUID().toString(true);
        //推送文件消息
        if(UserChannelManager.contains(receiverId)) {
            Channel receiverChannel = UserChannelManager.getChannel(receiverId);
            Frame frame = Frame.builder()
                               .sequenceId(id)
                               .senderId(senderId)
                               .receiverId(receiverId)
                               .type(FrameType.PRIVATE_FILE_MESSAGE.getType())
                               .extend(transferResult)
                               .build();
            receiverChannel.writeAndFlush(new Gson().toJson(frame));
        }
        //持久化消息
        PrivateChatMessage message = PrivateChatMessage.builder()
                                                     .senderId(senderId)
                                                     .sendTime(LocalDateTime.now())
                                                     .receiverId(receiverId)
                                                     .alreadyRead(MessageReadEnum.UNREAD.getStatus())
                                                     .type(MessageType.FILE.getCode())
                                                     .extendName(fileName)
                                                     .extendSize(size)
                                                     .build();
        message.setId(id);
        privateChatMapper.insert(message);
    }

    private void checkRelationShip(Long senderId, Long receiverId) {
        HashMap<String, Object> map = userService.preConditionSearchUser(senderId, receiverId);
        if(!map.get("status").equals(SearchUserStatusEnum.ALREADY_FRIENDS.getStatus())) {
            throw new RequestParamValidationException(ImmutableMap.of("cause", "非好友关系无法发送私信消息"));
        }
    }

    private void checkUserExist(Long senderId, Long receiverId) {
        if(checkUserExist(senderId) && checkUserExist(receiverId)) {
            throw new RequestParamValidationException(ImmutableMap.of("cause", "用户ID不存在"));
        }
    }

    private boolean checkUserExist(Long userId) {
        return userService.getById(userId) != null;
    }
}
