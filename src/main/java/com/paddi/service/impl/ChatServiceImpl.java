package com.paddi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.paddi.common.FrameType;
import com.paddi.common.MessageReadEnum;
import com.paddi.common.SearchUserStatusEnum;
import com.paddi.entity.vo.ChatHistoryVo;
import com.paddi.exception.AuthenticationException;
import com.paddi.exception.BaseException;
import com.paddi.mapper.PrivateChatMapper;
import com.paddi.message.Frame;
import com.paddi.message.PrivateChatMessage;
import com.paddi.service.ChatService;
import com.paddi.service.UserService;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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
}
