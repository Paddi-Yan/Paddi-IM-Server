package com.paddi.exception;

import com.paddi.common.ErrorCode;

import java.util.Map;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年12月08日 00:46:00
 */
public class BadRequestException extends BaseException{
    private static final long serialVersionUID = 4745556014784564136L;

    public BadRequestException(Map<String, Object> data) {
        super(ErrorCode.BAD_REQUEST, data);
    }

    public BadRequestException() {
        super(ErrorCode.BAD_REQUEST, null);
    }
}
