package com.picko.api.auth.application;

import com.picko.api.auth.application.dto.AuthServiceDto;
import com.picko.api.common.security.JwtProvider;
import com.picko.api.user.application.UserService;
import com.picko.api.user.application.dto.UserServiceDto;
import com.picko.api.user.domain.AuthProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final String REFRESH_TOKEN_PREFIX = "RT:";
    private static final long REFRESH_TOKEN_DAYS = 14;

    private final OAuthVerifierRegistry verifierRegistry;
    private final JwtProvider jwtProvider;
    private final StringRedisTemplate redisTemplate;
    private final UserService userService;

    public AuthServiceDto.TokenResponse login(AuthProvider provider, String token) {
        OAuthTokenVerifier.OAuthUserInfo userInfo = verifierRegistry.get(provider).verify(token);
        UserServiceDto.Response user = userService.findOrCreateByOAuth(
                provider, userInfo.email(), userInfo.name(), userInfo.sub());
        return issueTokens(user.getId(), user.getEmail());
    }

    public AuthServiceDto.TokenResponse refresh(String refreshToken) {
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("유효하지 않은 Refresh Token");
        }

        Long userId = jwtProvider.getUserIdFromToken(refreshToken);
        String stored = redisTemplate.opsForValue().get(REFRESH_TOKEN_PREFIX + userId);

        if (!refreshToken.equals(stored)) {
            throw new IllegalArgumentException("Refresh Token 불일치");
        }

        UserServiceDto.Response user = userService.getUser(userId);
        return issueTokens(userId, user.getEmail());
    }

    public void logout(Long userId) {
        redisTemplate.delete(REFRESH_TOKEN_PREFIX + userId);
    }

    @Transactional
    public void withdraw(Long userId) {
        userService.withdraw(userId);
        logout(userId);
    }

    private AuthServiceDto.TokenResponse issueTokens(Long userId, String email) {
        String accessToken = jwtProvider.generateAccessToken(userId, email);
        String refreshToken = jwtProvider.generateRefreshToken(userId);
        redisTemplate.opsForValue().set(
                REFRESH_TOKEN_PREFIX + userId, refreshToken, REFRESH_TOKEN_DAYS, TimeUnit.DAYS);
        return AuthServiceDto.TokenResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}
