package com.paddi.netty;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月25日 14:59:57
 */
@Slf4j
public class UserChannelManager {
    private static ConcurrentHashMap<Long, Channel> USER_CHANNEL_MAP = new ConcurrentHashMap<>();
    private static ConcurrentHashMap<Channel, Long> CHANNEL_USER_MAP = new ConcurrentHashMap<>();

    public static boolean put(Long senderId, Channel channel) {
        UserChannelManager.USER_CHANNEL_MAP.put(senderId, channel);
        UserChannelManager.CHANNEL_USER_MAP.put(channel, senderId);
        log.info("存储用户{}与客户端的连接:{}",senderId, channel);
        return UserChannelManager.USER_CHANNEL_MAP.get(senderId) != null && UserChannelManager.CHANNEL_USER_MAP.get(channel) != null;
    }

    public static Channel getChannel(Long senderId) {
        return UserChannelManager.USER_CHANNEL_MAP.get(senderId);
    }

    public static Long getSenderId(Channel channel) {
        return UserChannelManager.CHANNEL_USER_MAP.get(channel);
    }

    public static void remove(Long senderId) {
        Channel channel = UserChannelManager.USER_CHANNEL_MAP.remove(senderId);
        if(channel == null) {
            return;
        }
        UserChannelManager.CHANNEL_USER_MAP.remove(channel);
    }

    public static Long remove(Channel channel) throws Exception {
        Long senderId = UserChannelManager.CHANNEL_USER_MAP.remove(channel);
        if(senderId == null) {
            throw new Exception("移除用户绑定连接失败");
        }
        UserChannelManager.USER_CHANNEL_MAP.remove(senderId);
        log.info("移除连接用户{}与服务器的连接{}",senderId, channel);
        return senderId;
    }

    public static Boolean contains(Long userId) {
        return USER_CHANNEL_MAP.containsKey(userId);
    }
}
