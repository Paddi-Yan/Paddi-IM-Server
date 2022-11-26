package com.paddi.exception;

import com.paddi.common.ErrorCode;

import java.util.Map;

/**
 * @Project: SignWe
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月09日 15:22:57
 */
public class RequestParamValidationException extends BaseException{

    private static final long serialVersionUID = -6495622422694496133L;



    public RequestParamValidationException(Map<String, Object> data) {
        super(ErrorCode.REQUEST_VALIDATION_FAILED, data);
    }

    public RequestParamValidationException() {
        super(ErrorCode.REQUEST_VALIDATION_FAILED, null);
    }

}
