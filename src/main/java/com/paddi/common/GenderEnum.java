package com.paddi.common;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月25日 17:41:15
 */
public enum GenderEnum {
    MALE(1, "男"),
    FEMALE(2,"女");


    private int code;

    @EnumValue
    private String name;

    GenderEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public static int getCode(String name) {
        for(GenderEnum value : GenderEnum.values()) {
            if(value.name.equals(name)) {
                return value.code;
            }
        }
        throw new RuntimeException();
    }

    public static String getName(int code) {
        for(GenderEnum value : GenderEnum.values()) {
            if(value.getCode() == code) {
                return value.getName();
            }
        }
        throw new RuntimeException();
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
