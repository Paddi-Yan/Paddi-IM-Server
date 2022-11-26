package com.paddi.exception;

import com.paddi.common.ErrorCode;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月26日 13:38:42
 */
public class BaseException extends RuntimeException{
    private static final long serialVersionUID = 5501245689467498091L;

    private final ErrorCode error;

    private final HashMap<String, Object> data = new HashMap<>();

    public BaseException(ErrorCode error, Map<String, Object> data) {
        super(error.getMessage());
        this.error = error;
        if(!ObjectUtils.isEmpty(data)) {
            this.data.putAll(data);
        }
    }

    protected BaseException(ErrorCode error, Map<String, Object> data, Throwable cause) {
        super(error.getMessage(), cause);
        this.error = error;
        if (!ObjectUtils.isEmpty(data)) {
            this.data.putAll(data);
        }
    }
    public ErrorCode getError() {
        return error;
    }

    public Map<String, Object> getData() {
        return data;
    }
}
