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
 * @CreatedTime: 2022年11月26日 19:59:37
 */
public enum FriendRequestStatusEnum {
    NOT_HANDLE(1, "好友请求未处理"),
    REFUSED(2, "好友请求已经拒绝"),
    ACCEPTED(3, "好友请求已经接受");

    public final Integer code;

    @EnumValue
    public final String message;

    FriendRequestStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    private static Map<Integer, FriendRequestStatusEnum> map;

    static {
        map = Arrays.stream(FriendRequestStatusEnum.values())
                    .collect(Collectors.toMap(FriendRequestStatusEnum:: getCode, UnaryOperator.identity()));
    }

    public static Optional<FriendRequestStatusEnum> getFriendRequestStatusEnum(Integer status) {
        return Optional.of(map.get(status));
    }

    public Integer getCode() {
        return code;
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
