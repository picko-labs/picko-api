package com.picko.api.user.infrastructure;

import com.picko.api.user.domain.AuthProvider;
import com.picko.api.user.domain.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    Optional<UserEntity> findByEmail(String email);

    Optional<UserEntity> findByAuthProviderAndAuthProviderId(AuthProvider authProvider, String authProviderId);
}
