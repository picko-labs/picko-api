package com.picko.api.auth.application;

import com.picko.api.user.domain.AuthProvider;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class OAuthVerifierRegistry {

    private final Map<AuthProvider, OAuthTokenVerifier> verifiers;

    public OAuthVerifierRegistry(List<OAuthTokenVerifier> verifiers) {
        this.verifiers = verifiers.stream()
                .collect(Collectors.toMap(OAuthTokenVerifier::provider, v -> v));
    }

    public OAuthTokenVerifier get(AuthProvider provider) {
        OAuthTokenVerifier verifier = verifiers.get(provider);
        if (verifier == null) {
            throw new IllegalArgumentException("지원하지 않는 OAuth 공급자: " + provider);
        }
        return verifier;
    }
}
