package com.paddi.exception;

import com.paddi.common.ErrorCode;

import java.util.Map;

/**
 * @Project: SignWe
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月09日 15:12:38
 */
public class AuthenticationException extends BaseException {
    private static final long serialVersionUID = 9078002156513199544L;

    public AuthenticationException(Map<String, Object> data) {
        super(ErrorCode.UNAUTHORIZED, data);
    }

    public AuthenticationException() {
        super(ErrorCode.UNAUTHORIZED, null);
    }
}
