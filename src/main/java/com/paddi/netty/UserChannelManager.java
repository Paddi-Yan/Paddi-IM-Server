package com.paddi.netty;

import io.netty.channel.Channel;

import java.util.HashMap;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月25日 14:59:57
 */
public class UserChannelManager {
    private static HashMap<Long, Channel> USER_CHANNEL_MAP = new HashMap<>();
    private static HashMap<Channel, Long> CHANNEL_USER_MAP = new HashMap<>();
    public static boolean put(Long senderId, Channel channel) {
        Channel c = UserChannelManager.USER_CHANNEL_MAP.put(senderId, channel);
        Long s = UserChannelManager.CHANNEL_USER_MAP.put(channel, senderId);
        return c != null && s != null;
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

    public static void remove(Channel channel) {
        Long senderId = UserChannelManager.CHANNEL_USER_MAP.remove(channel);
        if(senderId == null) {
            return;
        }
        UserChannelManager.USER_CHANNEL_MAP.remove(senderId);
    }
}
