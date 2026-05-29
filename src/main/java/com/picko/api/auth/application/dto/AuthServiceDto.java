package com.picko.api.auth.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AuthServiceDto {

    @Schema(description = "OAuth 소셜 로그인 요청")
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OAuthLoginRequest {

        @Schema(description = "OAuth 공급자에서 발급받은 Token")
        private String token;
    }

    @Schema(description = "Refresh Token 갱신 요청")
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RefreshRequest {

        @Schema(description = "Refresh Token")
        private String refreshToken;
    }

    @Schema(description = "토큰 응답")
    @Getter
    @Builder
    public static class TokenResponse {

        @Schema(description = "Access Token (Bearer)")
        private String accessToken;

        @Schema(description = "Refresh Token")
        private String refreshToken;
    }
}
