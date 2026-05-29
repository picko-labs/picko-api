package com.picko.api.auth.application;

import com.picko.api.user.domain.AuthProvider;

public interface OAuthTokenVerifier {

    OAuthUserInfo verify(String token);

    AuthProvider provider();

    record OAuthUserInfo(String sub, String email, String name) {}
}
