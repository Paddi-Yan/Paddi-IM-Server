package com.paddi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.gson.Gson;
import com.paddi.common.FrameType;
import com.paddi.common.MessageReadEnum;
import com.paddi.mapper.PrivateChatMapper;
import com.paddi.message.Frame;
import com.paddi.message.PrivateChatMessage;
import com.paddi.service.ChatService;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月27日 15:55:20
 */
@Service
public class ChatServiceImpl implements ChatService {

    @Resource
    private PrivateChatMapper privateChatMapper;

    @Override
    public PrivateChatMessage sendPrivateMessage(PrivateChatMessage message) throws Exception {
        int insert = privateChatMapper.insert(message);
        if(insert == 0) {
            throw new Exception();
        }
        return message;
    }

    @Override
    public void sendUnreadMessage(Channel channel, Long userId) {
        List<PrivateChatMessage> messageList = privateChatMapper.selectList(new QueryWrapper<PrivateChatMessage>()
                .eq("receiver_id", userId).eq("is_read", MessageReadEnum.UNREAD.getStatus())
                .orderByDesc("send_time"));
        if(!messageList.isEmpty()) {
            Map<Long, List<PrivateChatMessage>> messageMap = messageList.stream()
                                                               .collect(Collectors.groupingBy(PrivateChatMessage::getSenderId));
            Frame frame = Frame.builder().receiverId(userId).type(FrameType.UNREAD_PRIVATE_MESSAGE)
                               .extend(messageMap).build();
            channel.writeAndFlush(new TextWebSocketFrame(new Gson().toJson(frame)));
        }
    }
}
