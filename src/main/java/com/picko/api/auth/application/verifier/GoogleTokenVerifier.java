package com.picko.api.auth.application.verifier;

import com.picko.api.auth.application.OAuthTokenVerifier;
import com.picko.api.user.domain.AuthProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class GoogleTokenVerifier implements OAuthTokenVerifier {

    private final RestClient restClient = RestClient.create();

    @Value("${google.client-id}")
    private String clientId;

    @Override
    public OAuthUserInfo verify(String token) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("ID Token이 비어 있습니다");
        }

        Map<String, Object> response;
        try {
            response = restClient.get()
                    .uri("https://oauth2.googleapis.com/tokeninfo?id_token={token}", token)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (Exception e) {
            throw new IllegalArgumentException("Google ID Token 검증 실패");
        }

        if (response == null || !clientId.equals(response.get("aud"))) {
            throw new IllegalArgumentException("유효하지 않은 Google ID Token");
        }

        return new OAuthUserInfo(
                (String) response.get("sub"),
                (String) response.get("email"),
                (String) response.get("name")
        );
    }

    @Override
    public AuthProvider provider() {
        return AuthProvider.GOOGLE;
    }
}
