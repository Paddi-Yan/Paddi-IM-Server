package com.paddi.common;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月26日 13:59:48
 */
public enum SearchUserStatusEnum {
    SUCCESS(0, "查询成功"),
    USER_NOT_EXIST(1, "查询不到该用户."),
    YOURSELF(2, "查询结果为自己"),
    ALREADY_FRIENDS(3, "该用户已经是你的好友");

    public final Integer status;

    public final String message;

    SearchUserStatusEnum(Integer status, String message) {
        this.status = status;
        this.message = message;
    }

    public Integer getStatus() {
        return status;
    }

    public static String getMsgByKey(Integer status) {
        for(SearchUserStatusEnum type : SearchUserStatusEnum.values()) {
            if(type.getStatus().equals(status)) {
                return type.message;
            }
        }
        return null;
    }
}
