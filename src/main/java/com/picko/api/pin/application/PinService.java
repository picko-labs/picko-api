package com.picko.api.pin.application;

import com.picko.api.common.exception.BusinessException;
import com.picko.api.common.exception.ErrorCode;
import com.picko.api.pin.application.dto.PinServiceDto;
import com.picko.api.pin.domain.UserPinCategoryEntity;
import com.picko.api.pin.domain.UserPinEntity;
import com.picko.api.pin.infrastructure.UserPinCategoryRepository;
import com.picko.api.pin.infrastructure.UserPinRepository;
import com.picko.api.spot.domain.SpotEntity;
import com.picko.api.spot.infrastructure.SpotRepository;
import com.picko.api.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PinService {

    private final UserPinCategoryRepository userPinCategoryRepository;
    private final UserPinRepository userPinRepository;
    private final UserRepository userRepository;
    private final SpotRepository spotRepository;

    // ── user_pin_categories (내부용) ──────────────────────────

    public List<PinServiceDto.UserPinCategoryInfo> getUserPinCategories(Long userId) {
        return userPinCategoryRepository.findByUserIdAndDeletedAtIsNullOrderBySortOrderAsc(userId)
                .stream()
                .map(c -> toCategoryInfo(c, 0L))
                .toList();
    }

    @Transactional
    public PinServiceDto.UserPinCategoryInfo createUserPinCategory(PinServiceDto.UserPinCategoryCreateRequest request) {
        var user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        UserPinCategoryEntity entity = UserPinCategoryEntity.create(user, request.getName(), request.getSortOrder());
        return toCategoryInfo(userPinCategoryRepository.save(entity), 0L);
    }

    @Transactional
    public PinServiceDto.UserPinCategoryInfo updateUserPinCategory(Long id, PinServiceDto.UserPinCategoryUpdateRequest request) {
        UserPinCategoryEntity entity = userPinCategoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PIN_CATEGORY_NOT_FOUND));
        entity.update(request.getName(), request.getSortOrder());
        return toCategoryInfo(entity, 0L);
    }

    @Transactional
    public void deleteUserPinCategory(Long id) {
        UserPinCategoryEntity entity = userPinCategoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PIN_CATEGORY_NOT_FOUND));
        userPinCategoryRepository.delete(entity);
    }

    // ── user_pins (내부용) ────────────────────────────────────

    public List<PinServiceDto.UserPinInfo> getUserPins(Long userId) {
        return userPinRepository.findByUserIdAndDeletedAtIsNull(userId)
                .stream()
                .map(this::toPinInfo)
                .toList();
    }

    @Transactional
    public PinServiceDto.UserPinInfo createUserPin(PinServiceDto.UserPinCreateRequest request) {
        if (userPinRepository.existsByUserIdAndSpotIdAndDeletedAtIsNull(request.getUserId(), request.getSpotId())) {
            throw new BusinessException(ErrorCode.PIN_ALREADY_EXISTS);
        }
        var user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        var spot = spotRepository.findById(request.getSpotId())
                .orElseThrow(() -> new BusinessException(ErrorCode.SPOT_NOT_FOUND));
        UserPinCategoryEntity category = null;
        if (request.getUserPinCategoryId() != null) {
            category = userPinCategoryRepository.findById(request.getUserPinCategoryId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.PIN_CATEGORY_NOT_FOUND));
        }
        return toPinInfo(userPinRepository.save(UserPinEntity.create(user, spot, category)));
    }

    @Transactional
    public PinServiceDto.UserPinInfo updateUserPin(Long id, PinServiceDto.UserPinUpdateRequest request) {
        UserPinEntity entity = userPinRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_PIN_NOT_FOUND));
        UserPinCategoryEntity category = null;
        if (request.getUserPinCategoryId() != null) {
            category = userPinCategoryRepository.findById(request.getUserPinCategoryId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.PIN_CATEGORY_NOT_FOUND));
        }
        entity.changeCategory(category);
        return toPinInfo(entity);
    }

    @Transactional
    public void deleteUserPin(Long id) {
        UserPinEntity entity = userPinRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_PIN_NOT_FOUND));
        userPinRepository.delete(entity);
    }

    // ── Pick (회원 전용) ──────────────────────────────────────

    /** 내 핀 카테고리 목록을 spotCount와 함께 반환한다. */
    public List<PinServiceDto.UserPinCategoryInfo> getPickCategories(Long userId) {
        List<UserPinCategoryEntity> categories =
                userPinCategoryRepository.findByUserIdAndDeletedAtIsNullOrderBySortOrderAsc(userId);
        List<UserPinEntity> pins = userPinRepository.findByUserIdAndDeletedAtIsNull(userId);

        // Hibernate proxy의 getId()는 FK 값을 바로 반환하므로 lazy load를 트리거하지 않는다.
        Map<Long, Long> spotCountByCategory = pins.stream()
                .filter(p -> p.getUserPinCategory() != null)
                .collect(Collectors.groupingBy(
                        p -> p.getUserPinCategory().getId(), Collectors.counting()));

        return categories.stream()
                .map(c -> toCategoryInfo(c, spotCountByCategory.getOrDefault(c.getId(), 0L)))
                .toList();
    }

    /** 내 핀 카테고리를 추가한다. sortOrder는 기존 카테고리 수로 자동 설정된다. */
    @Transactional
    public PinServiceDto.UserPinCategoryInfo createPickCategory(Long userId, PinServiceDto.PickCategoryCreateRequest request) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        int sortOrder = userPinCategoryRepository.findByUserIdAndDeletedAtIsNullOrderBySortOrderAsc(userId).size();
        UserPinCategoryEntity entity = UserPinCategoryEntity.create(user, request.getName(), sortOrder);
        return toCategoryInfo(userPinCategoryRepository.save(entity), 0L);
    }

    /**
     * 뷰포트 내 내가 핀한 스팟 목록을 반환한다.
     * categoryId가 주어지면 해당 카테고리의 핀만, null이면 전체 핀을 기준으로 필터링한다.
     */
    public List<PinServiceDto.PickSpotItem> getPickSpots(
            Long userId,
            java.math.BigDecimal swLat, java.math.BigDecimal swLng,
            java.math.BigDecimal neLat, java.math.BigDecimal neLng,
            Long categoryId) {

        List<SpotEntity> viewportSpots = spotRepository.findByViewport(swLat, swLng, neLat, neLng, null);
        List<UserPinEntity> userPins = userPinRepository.findByUserIdAndDeletedAtIsNull(userId);

        List<UserPinEntity> filteredPins = categoryId != null
                ? userPins.stream()
                        .filter(p -> p.getUserPinCategory() != null
                                && p.getUserPinCategory().getId().equals(categoryId))
                        .toList()
                : userPins;

        Map<Long, UserPinEntity> spotIdToPinMap = filteredPins.stream()
                .collect(Collectors.toMap(p -> p.getSpot().getId(), p -> p, (a, b) -> a));

        return viewportSpots.stream()
                .filter(s -> spotIdToPinMap.containsKey(s.getId()))
                .map(s -> toPickSpotItem(s, spotIdToPinMap.get(s.getId())))
                .toList();
    }

    // ─────────────────────────────────────────────────────────

    private PinServiceDto.UserPinCategoryInfo toCategoryInfo(UserPinCategoryEntity entity, Long spotCount) {
        return PinServiceDto.UserPinCategoryInfo.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .name(entity.getName())
                .sortOrder(entity.getSortOrder())
                .spotCount(spotCount)
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

    private PinServiceDto.PickSpotItem toPickSpotItem(SpotEntity spot, UserPinEntity pin) {
        var coordinate = spot.getSpotAddress() != null ? spot.getSpotAddress().getCoordinate() : null;
        return PinServiceDto.PickSpotItem.builder()
                .id(spot.getId())
                .name(spot.getName())
                .imageUrl(spot.getImageUrl())
                .isTrending(spot.getIsTrending())
                .latitude(coordinate != null ? coordinate.getLatitude() : null)
                .longitude(coordinate != null ? coordinate.getLongitude() : null)
                .userPinCategoryId(pin.getUserPinCategory() != null ? pin.getUserPinCategory().getId() : null)
                .build();
    }
}
