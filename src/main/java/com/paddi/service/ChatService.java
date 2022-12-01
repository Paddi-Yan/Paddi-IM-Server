package com.paddi.service;

import com.paddi.message.PrivateChatMessage;
import io.netty.channel.Channel;

import java.util.Map;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月27日 15:54:44
 */
public interface ChatService {

    PrivateChatMessage sendPrivateMessage(PrivateChatMessage message);

    void sendUnreadMessage(Channel channel, Long userId);

    void signMessageAlreadyRead(Map<String, Long> extend);
}
