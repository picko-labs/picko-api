package com.picko.api.service.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class UserServiceDto {

    @Schema(description = "사용자 등록/수정 요청")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {

        @Schema(description = "서비스 닉네임", example = "picko_user")
        private String name;

        @Schema(description = "로그인 이메일", example = "user@example.com")
        private String email;
    }

    @Schema(description = "사용자 응답")
    @Getter
    @Builder
    public static class Response {

        @Schema(description = "사용자 ID", example = "1")
        private Long id;

        @Schema(description = "서비스 닉네임", example = "picko_user")
        private String name;

        @Schema(description = "로그인 이메일", example = "user@example.com")
        private String email;
    }
}
