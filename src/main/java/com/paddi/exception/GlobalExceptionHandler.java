package com.paddi.exception;

import com.paddi.common.ErrorResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @Project: Paddi-IM-Server
 * @Author: Paddi-Yan
 * @CreatedTime: 2022年11月26日 13:43:47
 */
@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler(BaseException.class)
    public ResponseEntity<?> handleException(BaseException e, HttpServletRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(e, request.getRequestURI());
        return new ResponseEntity<>(errorResponse, new HttpHeaders(), e.getError().getStatus());
    }
}
