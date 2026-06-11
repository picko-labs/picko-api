package com.picko.api.spot.application;

import com.picko.api.admin.infrastructure.AdminRepository;
import com.picko.api.common.exception.BusinessException;
import com.picko.api.common.exception.ErrorCode;
import com.picko.api.pin.infrastructure.UserPinRepository;
import com.picko.api.spot.application.dto.SpotServiceDto;
import com.picko.api.spot.domain.SpotAddressEntity;
import com.picko.api.spot.domain.SpotCategoryEntity;
import com.picko.api.spot.domain.SpotCategoryMappingEntity;
import com.picko.api.spot.domain.SpotEntity;
import com.picko.api.spot.domain.SpotHashtagEntity;
import com.picko.api.spot.domain.id.SpotCategoryMappingId;
import com.picko.api.spot.domain.vo.Coordinate;
import com.picko.api.spot.infrastructure.SpotAddressRepository;
import com.picko.api.spot.infrastructure.SpotCategoryMappingRepository;
import com.picko.api.spot.infrastructure.SpotCategoryRepository;
import com.picko.api.spot.infrastructure.SpotHashtagMappingRepository;
import com.picko.api.spot.infrastructure.SpotRepository;
import com.picko.api.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SpotService {

    private final SpotRepository spotRepository;
    private final SpotAddressRepository spotAddressRepository;
    private final SpotCategoryRepository spotCategoryRepository;
    private final SpotCategoryMappingRepository categoryMappingRepository;
    private final SpotHashtagMappingRepository hashtagMappingRepository;
    private final UserPinRepository userPinRepository;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    /**
     * 필터 조건에 맞는 스팟 목록을 반환한다.
     * 모든 파라미터는 선택적이며 null이면 해당 조건을 무시한다.
     */
    public List<SpotServiceDto.ListItem> getSpots(
            String addressCode, Boolean isTrending, String categoryCode, String hashtagCode) {
        return spotRepository.findByFilters(addressCode, isTrending, categoryCode, hashtagCode)
                .stream()
                .map(this::toListItem)
                .toList();
    }

    /**
     * 스팟 단건 상세 정보를 반환한다.
     * 존재하지 않는 id면 BusinessException(SPOT_NOT_FOUND)을 던진다.
     */
    public SpotServiceDto.Detail getSpot(Long id) {
        SpotEntity spot = spotRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.SPOT_NOT_FOUND));
        return toDetail(spot);
    }

    /**
     * 스팟 목록 아이템 DTO로 변환한다.
     * 좌표는 spotAddress를 통해 접근한다. spotAddress가 미분류(null)이면 좌표도 null로 반환된다.
     * pinCount는 비정규화하지 않으므로 매 호출마다 user_pins를 집계한다.
     */
    private SpotServiceDto.ListItem toListItem(SpotEntity spot) {
        List<SpotServiceDto.CategoryInfo> categories = categoryMappingRepository
                .findByIdSpotId(spot.getId())
                .stream()
                .map(m -> toCategoryInfo(m.getSpotCategory()))
                .toList();

        var coordinate = spot.getSpotAddress() != null ? spot.getSpotAddress().getCoordinate() : null;
        return SpotServiceDto.ListItem.builder()
                .id(spot.getId())
                .name(spot.getName())
                .imageUrl(spot.getImageUrl())
                .isTrending(spot.getIsTrending())
                .latitude(coordinate != null ? coordinate.getLatitude() : null)
                .longitude(coordinate != null ? coordinate.getLongitude() : null)
                .categories(categories)
                .pinCount(userPinRepository.countBySpotIdAndDeletedAtIsNull(spot.getId()))
                .build();
    }

    /**
     * 스팟 상세 DTO로 변환한다.
     * 목록과 달리 해시태그 정보를 포함하며, 주소 전체 정보(region/city/town/좌표 등)를 AddressInfo에 담아 반환한다.
     */
    private SpotServiceDto.Detail toDetail(SpotEntity spot) {
        List<SpotServiceDto.CategoryInfo> categories = categoryMappingRepository
                .findByIdSpotId(spot.getId())
                .stream()
                .map(m -> toCategoryInfo(m.getSpotCategory()))
                .toList();

        List<SpotServiceDto.HashtagInfo> hashtags = hashtagMappingRepository
                .findByIdSpotId(spot.getId())
                .stream()
                .map(m -> toHashtagInfo(m.getSpotHashtag()))
                .toList();

        return SpotServiceDto.Detail.builder()
                .id(spot.getId())
                .name(spot.getName())
                .description(spot.getDescription())
                .isTrending(spot.getIsTrending())
                .imageUrl(spot.getImageUrl())
                .spotAddress(spot.getSpotAddress() != null ? toAddressInfo(spot.getSpotAddress()) : null)
                .categories(categories)
                .hashtags(hashtags)
                .pinCount(userPinRepository.countBySpotIdAndDeletedAtIsNull(spot.getId()))
                .createdAt(spot.getCreatedAt())
                .build();
    }

    /** 카테고리 엔티티를 응답 DTO로 변환한다. */
    private SpotServiceDto.CategoryInfo toCategoryInfo(SpotCategoryEntity category) {
        return SpotServiceDto.CategoryInfo.builder()
                .id(category.getId())
                .code(category.getCode())
                .name(category.getName())
                .icon(category.getIcon())
                .build();
    }

    /** 해시태그 엔티티를 응답 DTO로 변환한다. */
    private SpotServiceDto.HashtagInfo toHashtagInfo(SpotHashtagEntity hashtag) {
        return SpotServiceDto.HashtagInfo.builder()
                .id(hashtag.getId())
                .code(hashtag.getCode())
                .name(hashtag.getName())
                .icon(hashtag.getIcon())
                .build();
    }

    // ── spots 저장 ────────────────────────────────────────────

    /**
     * 새 스팟을 저장한다.
     * userId / adminId 중 하나만 세팅하며, 둘 다 null이면 배치 등록으로 처리된다.
     */
    public SpotServiceDto.Detail createSpot(SpotServiceDto.SpotCreateRequest request) {
        SpotEntity spot = SpotEntity.create(
                request.getName(),
                request.getDescription(),
                request.getImageUrl(),
                request.getIsTrending() != null ? request.getIsTrending() : false);

        if (request.getSpotAddressId() != null) {
            SpotAddressEntity address = spotAddressRepository.findById(request.getSpotAddressId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.SPOT_ADDRESS_NOT_FOUND));
            spot.assignAddress(address);
        }

        if (request.getUserId() != null) {
            spot.registerByUser(userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND)));
        }

        if (request.getAdminId() != null) {
            spot.registerByAdmin(adminRepository.findById(request.getAdminId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_NOT_FOUND)));
        }

        return toDetail(spotRepository.save(spot));
    }

    // ── spot_address CRUD ─────────────────────────────────────

    /** 삭제되지 않은 전체 주소 목록을 반환한다. */
    public List<SpotServiceDto.AddressInfo> getSpotAddresses() {
        return spotAddressRepository.findAllByDeletedAtIsNull()
                .stream()
                .map(this::toAddressInfo)
                .toList();
    }

    /** 새 주소를 생성한다. */
    @Transactional
    public SpotServiceDto.AddressInfo createSpotAddress(SpotServiceDto.SpotAddressRequest request) {
        SpotAddressEntity entity = SpotAddressEntity.create(
                request.getCode(), request.getRegion(), request.getCity(), request.getTown(),
                request.getPostalCode(), request.getAddress(), request.getAddressDetail(),
                toCoordinate(request));
        return toAddressInfo(spotAddressRepository.save(entity));
    }

    /** 기존 주소를 수정한다. */
    @Transactional
    public SpotServiceDto.AddressInfo updateSpotAddress(Long id, SpotServiceDto.SpotAddressRequest request) {
        SpotAddressEntity entity = spotAddressRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.SPOT_ADDRESS_NOT_FOUND));
        entity.update(
                request.getCode(), request.getRegion(), request.getCity(), request.getTown(),
                request.getPostalCode(), request.getAddress(), request.getAddressDetail(),
                toCoordinate(request));
        return toAddressInfo(entity);
    }

    /** 주소를 soft delete한다. */
    @Transactional
    public void deleteSpotAddress(Long id) {
        SpotAddressEntity entity = spotAddressRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.SPOT_ADDRESS_NOT_FOUND));
        spotAddressRepository.delete(entity);
    }

    private Coordinate toCoordinate(SpotServiceDto.SpotAddressRequest request) {
        if (request.getLatitude() != null && request.getLongitude() != null) {
            return new Coordinate(request.getLatitude(), request.getLongitude());
        }
        return null;
    }

    // ── spot_categories CRUD ──────────────────────────────────

    /** 삭제되지 않은 전체 카테고리를 노출 순서대로 반환한다. */
    public List<SpotServiceDto.CategoryInfo> getSpotCategories() {
        return spotCategoryRepository.findAllByDeletedAtIsNullOrderBySortOrderAsc()
                .stream()
                .map(this::toCategoryInfo)
                .toList();
    }

    /** 새 카테고리를 생성한다. */
    @Transactional
    public SpotServiceDto.CategoryInfo createSpotCategory(SpotServiceDto.SpotCategoryRequest request) {
        SpotCategoryEntity entity = SpotCategoryEntity.create(
                request.getCode(), request.getName(), request.getIcon(), request.getSortOrder());
        return toCategoryInfo(spotCategoryRepository.save(entity));
    }

    /** 기존 카테고리를 수정한다. */
    @Transactional
    public SpotServiceDto.CategoryInfo updateSpotCategory(Long id, SpotServiceDto.SpotCategoryRequest request) {
        SpotCategoryEntity entity = spotCategoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.SPOT_CATEGORY_NOT_FOUND));
        entity.update(request.getCode(), request.getName(), request.getIcon(), request.getSortOrder());
        return toCategoryInfo(entity);
    }

    /** 카테고리를 soft delete한다. */
    @Transactional
    public void deleteSpotCategory(Long id) {
        SpotCategoryEntity entity = spotCategoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.SPOT_CATEGORY_NOT_FOUND));
        spotCategoryRepository.delete(entity);
    }

    // ── spot_category_mappings CRUD ───────────────────────────

    /**
     * 스팟에 카테고리를 연결한다.
     * 이미 연결된 경우 중복 저장을 막기 위해 존재 여부를 먼저 확인한다.
     */
    @Transactional
    public void addCategoryToSpot(Long spotId, Long categoryId) {
        SpotCategoryMappingId mappingId = new SpotCategoryMappingId(spotId, categoryId);
        if (categoryMappingRepository.existsById(mappingId)) {
            return;
        }
        SpotEntity spot = spotRepository.findById(spotId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SPOT_NOT_FOUND));
        SpotCategoryEntity category = spotCategoryRepository.findById(categoryId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SPOT_CATEGORY_NOT_FOUND));
        categoryMappingRepository.save(new SpotCategoryMappingEntity(spot, category));
    }

    /** 스팟에서 카테고리 연결을 해제한다 (soft delete). */
    @Transactional
    public void removeCategoryFromSpot(Long spotId, Long categoryId) {
        SpotCategoryMappingId mappingId = new SpotCategoryMappingId(spotId, categoryId);
        SpotCategoryMappingEntity mapping = categoryMappingRepository.findById(mappingId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SPOT_CATEGORY_MAPPING_NOT_FOUND));
        categoryMappingRepository.delete(mapping);
    }

    // ─────────────────────────────────────────────────────────

    /**
     * 주소 엔티티를 응답 DTO로 변환한다.
     * 좌표는 Coordinate VO에서 꺼내 flat하게 노출한다.
     */
    private SpotServiceDto.AddressInfo toAddressInfo(SpotAddressEntity address) {
        var coordinate = address.getCoordinate();
        return SpotServiceDto.AddressInfo.builder()
                .id(address.getId())
                .code(address.getCode())
                .region(address.getRegion())
                .city(address.getCity())
                .town(address.getTown())
                .postalCode(address.getPostalCode())
                .address(address.getAddress())
                .addressDetail(address.getAddressDetail())
                .latitude(coordinate != null ? coordinate.getLatitude() : null)
                .longitude(coordinate != null ? coordinate.getLongitude() : null)
                .build();
    }
}
