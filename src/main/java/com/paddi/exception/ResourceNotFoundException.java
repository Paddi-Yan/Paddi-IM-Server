package com.paddi.exception;

import com.paddi.common.ErrorCode;

import java.util.Map;

/**
 * @Project: SignWe
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月09日 15:06:02
 */
public class ResourceNotFoundException extends BaseException {

    private static final long serialVersionUID = 4948121752707476985L;


    public ResourceNotFoundException(Map<String, Object> data) {
        super(ErrorCode.RESOURCE_NOT_FOUND, data);
    }
}
