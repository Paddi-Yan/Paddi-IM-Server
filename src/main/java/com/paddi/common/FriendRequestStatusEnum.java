package com.paddi.common;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月26日 19:59:37
 */
public enum FriendRequestStatusEnum {
    REFUSED(-1, "好友请求已经拒绝."),
    NOT_HANDLE(0, "好友请求未处理"),
    ACCEPTED(1, "好友请求已经接收");

    public final Integer status;

    public final String message;

    FriendRequestStatusEnum(Integer status, String message){
        this.status = status;
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public static String getMsgByKey(Integer status) {
        for (SearchUserStatusEnum type : SearchUserStatusEnum.values()) {
            if (type.getStatus().equals(status)) {
                return type.message;
            }
        }
        return null;
    }
}
