package com.paddi.common;

import com.baomidou.mybatisplus.annotation.EnumValue;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月25日 17:41:15
 */
public enum GenderEnum {
    MALE(1, "男"),
    FEMALE(2, "女"),
    UNKNOWN(3, "暂未设定")

    ;


    private int code;

    @EnumValue
    private String name;

    GenderEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    private static Map<String, GenderEnum> map;

    static {
        GenderEnum.map = Arrays.stream(GenderEnum.values())
                               .collect(Collectors.toMap(GenderEnum :: getName, UnaryOperator.identity()));
    }

    public static Optional<GenderEnum> getGenderEnum(String name) {
        return Optional.of(GenderEnum.map.get(name));
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
