package com.paddi.common;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年12月08日 13:07:52
 */
public enum GolangInvitationHandleType {
    REFUSE(0, "拒绝邀请"),
    ACCEPT(1, "接收邀请");
    private Integer code;
    private String type;

    GolangInvitationHandleType(Integer code, String type) {
        this.code = code;
        this.type = type;
    }

    public Integer getCode() {
        return code;
    }

    public String getType() {
        return type;
    }
}
