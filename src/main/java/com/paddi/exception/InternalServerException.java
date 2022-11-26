package com.paddi.exception;

import com.paddi.common.ErrorCode;

import java.util.Map;

/**
 * @Project: SignWe
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月09日 15:30:25
 */
public class InternalServerException extends BaseException{
    private static final long serialVersionUID = -4371638586274769255L;


    public InternalServerException(Map<String, Object> data) {
        super(ErrorCode.ERROR, data);
    }

    public InternalServerException() {
        super(ErrorCode.ERROR, null);
    }

}
