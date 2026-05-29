package com.picko.api.auth.presentation;

import com.picko.api.auth.application.AuthService;
import com.picko.api.auth.application.dto.AuthServiceDto;
import com.picko.api.common.security.UserPrincipal;
import com.picko.api.user.domain.AuthProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "소셜 로그인/회원가입", description = "provider: google | apple | line")
    @PostMapping("/{provider}")
    public ResponseEntity<AuthServiceDto.TokenResponse> login(
            @PathVariable String provider,
            @RequestBody AuthServiceDto.OAuthLoginRequest request) {
        AuthProvider authProvider = AuthProvider.valueOf(provider.toUpperCase());
        return ResponseEntity.ok(authService.login(authProvider, request.getToken()));
    }

    @Operation(summary = "Access Token 갱신")
    @PostMapping("/refresh")
    public ResponseEntity<AuthServiceDto.TokenResponse> refresh(
            @RequestBody AuthServiceDto.RefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request.getRefreshToken()));
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@AuthenticationPrincipal UserPrincipal principal) {
        authService.logout(principal.getId());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "회원탈퇴")
    @DeleteMapping("/me")
    public ResponseEntity<Void> withdraw(@AuthenticationPrincipal UserPrincipal principal) {
        authService.withdraw(principal.getId());
        return ResponseEntity.noContent().build();
    }
}
