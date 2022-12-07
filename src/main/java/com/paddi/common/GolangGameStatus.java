package com.paddi.common;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年12月07日 19:13:16
 */
public enum GolangGameStatus {
    WAITING(1, "等待对方接收邀请"),
    ALL_IN(2, "双方均已经进入对局房间"),
    SOMEONE_ALREADY(3, "一方已经就绪"),
    START(4, "双方均已经就绪, 对局开始"),
    END(5, "对局结束")
    ;



    private Integer code;

    private String status;

    GolangGameStatus(Integer code, String status) {
        this.code = code;
        this.status = status;
    }
}
