package com.picko.api.user.application.dto;

import com.picko.api.user.domain.AuthProvider;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

public class UserServiceDto {

    @Schema(description = "사용자 전체 정보 응답")
    @Getter
    @Builder
    public static class Response {

        @Schema(description = "사용자 ID")
        private Long id;

        @Schema(description = "서비스 닉네임")
        private String name;

        @Schema(description = "로그인 이메일")
        private String email;

        @Schema(description = "OAuth 공급자", example = "GOOGLE")
        private AuthProvider authProvider;

        @Schema(description = "국적 (ISO 3166-1 alpha-2)", example = "KR")
        private String nationality;

        @Schema(description = "한국 거주 인증 여부")
        private Boolean livingInKorea;

        @Schema(description = "가입 일시")
        private LocalDateTime createdAt;
    }
}
