package com.paddi.common;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月25日 14:28:23
 */
public enum FrameType {

    CONNECT(1, "初始化连接或重新建立连接请求消息"),
    PRIVATE_CHAT(2, "私信消息"),
    GROUP_CHAT(3, "群聊消息"),
    KEEPALIVE(4, "心跳消息包"),
    CLOSE(5, "关闭连接"),
    PRIVATE_CHAT_SUCCESS_RESPONSE(6, "私信消息发送成功"),
    PRIVATE_CHAT_FAIL_RESPONSE(7, "私信消息发送失败"),
    UNREAD_PRIVATE_MESSAGE(8, "未读消息"),
    AUTHORIZATION_WARNING_MESSAGE(9, "非好友关系,无权限发送消息"),
    PRIVATE_FILE_MESSAGE(10, "私信文件传输"),
    FRIEND_LIST(11, "好友列表"),

    READ_PRIVATE_MESSAGE(12, "标记为已读私聊消息"),
    FORBIDDEN(13, "用户权限不足,无法使用该功能,请联系管理员后重试"),
    FRIEND_ONLINE(14, "好友上线,更新好友列表"),
    FRIEND_OFFLINE(15, "好友下线,更新好友列表"),
    ;

    public final Integer type;
    public final String description;

    FrameType(Integer type, String description) {
        this.type = type;
        this.description = description;
    }

    public Integer getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "FrameType{" +
                "type=" + type +
                ", description='" + description + '\'' +
                '}';
    }
}
