package com.paddi.common;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月30日 20:48:17
 */
public enum MessageType {
    TEXT(1, "文本类型"),
    FILE(2, "文件类型");


    private int code;

    private String type;

    MessageType(int code, String type) {
        this.code = code;
        this.type = type;
    }

    public int getCode() {
        return code;
    }

    public String getType() {
        return type;
    }
}
