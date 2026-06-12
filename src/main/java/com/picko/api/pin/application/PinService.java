package com.picko.api.pin.application;

import com.picko.api.pin.application.dto.PinServiceDto;
import com.picko.api.pin.domain.UserPinCategoryEntity;
import com.picko.api.pin.domain.UserPinEntity;
import com.picko.api.pin.infrastructure.UserPinCategoryRepository;
import com.picko.api.pin.infrastructure.UserPinRepository;
import com.picko.api.spot.infrastructure.SpotRepository;
import com.picko.api.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PinService {

    private final UserPinCategoryRepository userPinCategoryRepository;
    private final UserPinRepository userPinRepository;
    private final UserRepository userRepository;
    private final SpotRepository spotRepository;

    // ── user_pin_categories ───────────────────────────────────

    public List<PinServiceDto.UserPinCategoryInfo> getUserPinCategories(Long userId) {
        return userPinCategoryRepository.findByUserIdOrderBySortOrderAsc(userId)
                .stream()
                .map(this::toCategoryInfo)
                .toList();
    }

    @Transactional
    public PinServiceDto.UserPinCategoryInfo createUserPinCategory(PinServiceDto.UserPinCategoryCreateRequest request) {
        var user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.getUserId()));
        UserPinCategoryEntity entity = UserPinCategoryEntity.create(user, request.getName(), request.getSortOrder());
        return toCategoryInfo(userPinCategoryRepository.save(entity));
    }

    @Transactional
    public PinServiceDto.UserPinCategoryInfo updateUserPinCategory(Long id, PinServiceDto.UserPinCategoryUpdateRequest request) {
        UserPinCategoryEntity entity = userPinCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("UserPinCategory not found: " + id));
        entity.update(request.getName(), request.getSortOrder());
        return toCategoryInfo(entity);
    }

    @Transactional
    public void deleteUserPinCategory(Long id) {
        UserPinCategoryEntity entity = userPinCategoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("UserPinCategory not found: " + id));
        userPinCategoryRepository.delete(entity);
    }

    // ── user_pins ─────────────────────────────────────────────

    public List<PinServiceDto.UserPinInfo> getUserPins(Long userId) {
        return userPinRepository.findByUserIdAndDeletedAtIsNull(userId)
                .stream()
                .map(this::toPinInfo)
                .toList();
    }

    @Transactional
    public PinServiceDto.UserPinInfo createUserPin(PinServiceDto.UserPinCreateRequest request) {
        if (userPinRepository.existsByUserIdAndSpotIdAndDeletedAtIsNull(request.getUserId(), request.getSpotId())) {
            throw new IllegalArgumentException("Pin already exists: userId=" + request.getUserId() + ", spotId=" + request.getSpotId());
        }
        UserPinEntity entity = new UserPinEntity();
        entity.setUser(userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + request.getUserId())));
        entity.setSpot(spotRepository.findById(request.getSpotId())
                .orElseThrow(() -> new IllegalArgumentException("Spot not found: " + request.getSpotId())));
        if (request.getUserPinCategoryId() != null) {
            entity.setUserPinCategory(userPinCategoryRepository.findById(request.getUserPinCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("UserPinCategory not found: " + request.getUserPinCategoryId())));
        }
        return toPinInfo(userPinRepository.save(entity));
    }

    @Transactional
    public PinServiceDto.UserPinInfo updateUserPin(Long id, PinServiceDto.UserPinUpdateRequest request) {
        UserPinEntity entity = userPinRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("UserPin not found: " + id));
        if (request.getUserPinCategoryId() != null) {
            entity.setUserPinCategory(userPinCategoryRepository.findById(request.getUserPinCategoryId())
                    .orElseThrow(() -> new IllegalArgumentException("UserPinCategory not found: " + request.getUserPinCategoryId())));
        } else {
            entity.setUserPinCategory(null);
        }
        return toPinInfo(entity);
    }

    @Transactional
    public void deleteUserPin(Long id) {
        UserPinEntity entity = userPinRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new IllegalArgumentException("UserPin not found: " + id));
        userPinRepository.delete(entity);
    }

    // ─────────────────────────────────────────────────────────

    private PinServiceDto.UserPinCategoryInfo toCategoryInfo(UserPinCategoryEntity entity) {
        return PinServiceDto.UserPinCategoryInfo.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .name(entity.getName())
                .sortOrder(entity.getSortOrder())
                .build();
    }

    private PinServiceDto.UserPinInfo toPinInfo(UserPinEntity entity) {
        return PinServiceDto.UserPinInfo.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .spotId(entity.getSpot().getId())
                .userPinCategoryId(entity.getUserPinCategory() != null ? entity.getUserPinCategory().getId() : null)
                .build();
    }
}
