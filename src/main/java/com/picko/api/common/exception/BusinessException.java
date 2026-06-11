package com.picko.api.common.exception;

import lombok.Getter;

/**
 * 비즈니스 규칙 위반을 표현하는 예외.
 * GlobalExceptionHandler 가 ErrorCode 의 HTTP 상태로 변환한다.
 */
@Getter
public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
