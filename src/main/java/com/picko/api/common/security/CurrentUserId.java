package com.picko.api.common.security;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 현재 인증된 사용자의 id(Long)를 컨트롤러 파라미터로 주입한다.
 * 비회원(익명) 요청이면 null 을 주입한다 — permitAll 엔드포인트의 개인화 분기용.
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentUserId {
}
