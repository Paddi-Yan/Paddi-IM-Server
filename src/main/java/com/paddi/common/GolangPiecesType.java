package com.paddi.common;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年12月07日 19:04:14
 */
public enum GolangPiecesType {
    WHITE_PIECES(-1, "白棋"),
    BLACK_PIECES(1, "黑棋")
    ;

    private Integer code;
    private String type;

    GolangPiecesType(int code, String type) {
        this.code = code;
        this.type = type;
    }

    @Override
    public String toString() {
        return "GolangPiecesType{" +
                "code=" + code +
                ", type='" + type + '\'' +
                '}';
    }
}
