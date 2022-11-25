package com.paddi.message;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月24日 14:18:40
 */
@Data
@ToString
public abstract class AbstractMessage implements Serializable {

    private static final long serialVersionUID = 1487010712144135820L;

    private String id;

    private int messageType;

    public static final int PRIVATE_MESSAGE = 1;

    public static final int GROUP_MESSAGE = 2;



    private static final Map<Integer, Class<?>> messageClasses = new HashMap<>();

    /**
     * 返回消息类型
     * @return MessageType
     */
    public abstract int getMessageType();

    public static Class<?> getMessageClass(int messageType) {
        return AbstractMessage.messageClasses.get(messageType);
    }

    static {
        AbstractMessage.messageClasses.put(AbstractMessage.PRIVATE_MESSAGE, PrivateChatMessage.class);
    }


}
