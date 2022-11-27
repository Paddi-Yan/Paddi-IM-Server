package com.paddi.common;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月27日 15:47:46
 */
public enum MessageReadEnum {
    UNREAD(false, "消息未读"),
    READ(true, "消息已读");

    private boolean status;

    private String name;

    MessageReadEnum(boolean code, String name) {
        this.status = code;
        this.name = name;
    }

    public boolean getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }
}
