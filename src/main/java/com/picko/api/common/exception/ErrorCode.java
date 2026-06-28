package com.picko.api.common.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 비즈니스 에러 코드 정의.
 * enum 이름이 응답 error.code 로 노출된다.
 */
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 400 Bad Request
    INVALID_INPUT(HttpStatus.BAD_REQUEST, "요청 값이 올바르지 않습니다."),
    UNSUPPORTED_OAUTH_PROVIDER(HttpStatus.BAD_REQUEST, "지원하지 않는 OAuth 공급자입니다."),

    // 401 Unauthorized
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),

    // 404 Not Found
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    ADMIN_NOT_FOUND(HttpStatus.NOT_FOUND, "어드민을 찾을 수 없습니다."),
    SPOT_NOT_FOUND(HttpStatus.NOT_FOUND, "스팟을 찾을 수 없습니다."),
    SPOT_ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "주소를 찾을 수 없습니다."),
    SPOT_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다."),
    SPOT_CATEGORY_MAPPING_NOT_FOUND(HttpStatus.NOT_FOUND, "카테고리 매핑을 찾을 수 없습니다."),
    SPOT_HASHTAG_NOT_FOUND(HttpStatus.NOT_FOUND, "해시태그를 찾을 수 없습니다."),
    SPOT_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "스팟 신청을 찾을 수 없습니다."),
    PIN_CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "핀 카테고리를 찾을 수 없습니다."),
    USER_PIN_NOT_FOUND(HttpStatus.NOT_FOUND, "핀을 찾을 수 없습니다."),

    // 409 Conflict
    PIN_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 핀한 스팟입니다."),

    // 500 Internal Server Error
    INTERNAL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 오류가 발생했습니다.");

    private final HttpStatus status;
    private final String message;
}
