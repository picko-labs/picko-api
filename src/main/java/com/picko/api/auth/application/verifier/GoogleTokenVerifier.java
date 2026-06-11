package com.picko.api.auth.application.verifier;

import com.picko.api.auth.application.OAuthTokenVerifier;
import com.picko.api.common.exception.BusinessException;
import com.picko.api.common.exception.ErrorCode;
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
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        Map<String, Object> response;
        try {
            response = restClient.get()
                    .uri("https://oauth2.googleapis.com/tokeninfo?id_token={token}", token)
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {});
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }

        if (response == null || !clientId.equals(response.get("aud"))) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
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
