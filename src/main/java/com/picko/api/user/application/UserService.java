package com.picko.api.user.application;

import com.picko.api.common.exception.BusinessException;
import com.picko.api.common.exception.ErrorCode;
import com.picko.api.user.application.dto.UserServiceDto;
import com.picko.api.user.domain.AuthProvider;
import com.picko.api.user.domain.UserEntity;
import com.picko.api.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public UserServiceDto.Response getUser(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return toResponse(user);
    }

    @Transactional
    public UserServiceDto.Response findOrCreateByOAuth(AuthProvider provider, String email, String name, String sub) {
        UserEntity user = userRepository
                .findByAuthProviderAndAuthProviderId(provider, sub)
                .orElseGet(() -> userRepository.save(
                        UserEntity.ofOAuth(provider, email, name, sub)));
        return toResponse(user);
    }

    @Transactional
    public void withdraw(Long userId) {
        userRepository.deleteById(userId);
    }

    private UserServiceDto.Response toResponse(UserEntity user) {
        return UserServiceDto.Response.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .authProvider(user.getAuthProvider())
                .nationality(user.getNationality())
                .livingInKorea(user.getLivingInKorea())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
