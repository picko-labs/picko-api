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
import com.picko.api.spot.domain.SpotHashtagMappingEntity;
import com.picko.api.spot.domain.SpotRequestEntity;
import com.picko.api.spot.domain.id.SpotCategoryMappingId;
import com.picko.api.spot.domain.vo.Coordinate;
import com.picko.api.spot.infrastructure.SpotAddressRepository;
import com.picko.api.spot.infrastructure.SpotCategoryMappingRepository;
import com.picko.api.spot.infrastructure.SpotCategoryRepository;
import com.picko.api.spot.infrastructure.SpotHashtagMappingRepository;
import com.picko.api.spot.infrastructure.SpotHashtagRepository;
import com.picko.api.spot.infrastructure.SpotRepository;
import com.picko.api.spot.infrastructure.SpotRequestRepository;
import com.picko.api.user.infrastructure.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SpotService {

    private final SpotRepository spotRepository;
    private final SpotAddressRepository spotAddressRepository;
    private final SpotCategoryRepository spotCategoryRepository;
    private final SpotCategoryMappingRepository categoryMappingRepository;
    private final SpotHashtagRepository spotHashtagRepository;
    private final SpotHashtagMappingRepository hashtagMappingRepository;
    private final SpotRequestRepository spotRequestRepository;
    private final UserPinRepository userPinRepository;
    private final UserRepository userRepository;
    private final AdminRepository adminRepository;

    /**
     * 뷰포트 내 스팟 목록을 반환한다.
     * 비회원(userId == null)은 지도 기본정보만, 회원은 개인화(isPinned)를 포함한다.
     */
    public List<SpotServiceDto.ListItem> getSpots(SpotServiceDto.ViewportRequest request, Long userId) {
        Set<Long> pinnedSpotIds = pinnedSpotIdsOf(userId);

        return spotRepository.findByViewport(
                        request.getSwLat(), request.getSwLng(),
                        request.getNeLat(), request.getNeLng(),
                        request.getCategoryCode())
                .stream()
                .map(spot -> toListItem(spot,
                        userPinRepository.countBySpotIdAndDeletedAtIsNull(spot.getId()),
                        pinnedSpotIds.contains(spot.getId())))
                .sorted(Comparator.comparingLong(SpotServiceDto.ListItem::getPinCount).reversed())
                .limit(20)
                .toList();
    }

    /**
     * 스팟 단건 상세 정보를 반환한다.
     * 존재하지 않는 id면 BusinessException(SPOT_NOT_FOUND)을 던진다.
     * 비회원(userId == null)은 isPinned == false 로 응답한다.
     */
    public SpotServiceDto.Detail getSpot(Long id, Long userId) {
        SpotEntity spot = spotRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.SPOT_NOT_FOUND));
        boolean isPinned = userId != null
                && userPinRepository.existsByUserIdAndSpotIdAndDeletedAtIsNull(userId, id);
        return toDetail(spot, isPinned);
    }

    /** 회원이면 핀한 spotId 집합을, 비회원이면 빈 집합을 반환한다 (개인화 isPinned 판정용). */
    private Set<Long> pinnedSpotIdsOf(Long userId) {
        return userId == null
                ? Set.of()
                : new HashSet<>(userPinRepository.findPinnedSpotIds(userId));
    }

    private SpotServiceDto.ListItem toListItem(SpotEntity spot, long pinCount, boolean isPinned) {
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
                .pinCount(pinCount)
                .isPinned(isPinned)
                .build();
    }

    /**
     * 스팟 상세 DTO로 변환한다.
     * 목록과 달리 해시태그 정보를 포함하며, 주소 전체 정보(region/city/town/좌표 등)를 AddressInfo에 담아 반환한다.
     */
    private SpotServiceDto.Detail toDetail(SpotEntity spot, boolean isPinned) {
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
                .isPinned(isPinned)
                .createdAt(spot.getCreatedAt())
                .build();
    }

    /** 카테고리 엔티티를 flat DTO로 변환한다 (스팟 목록·상세 응답용). */
    private SpotServiceDto.CategoryInfo toCategoryInfo(SpotCategoryEntity category) {
        return SpotServiceDto.CategoryInfo.builder()
                .id(category.getId())
                .code(category.getCode())
                .name(category.getName())
                .icon(category.getIcon())
                .build();
    }

    /** 카테고리 엔티티를 자식 포함 트리 DTO로 변환한다 (카테고리 목록 조회용). */
    private SpotServiceDto.CategoryInfo toTreeCategoryInfo(SpotCategoryEntity category) {
        return SpotServiceDto.CategoryInfo.builder()
                .id(category.getId())
                .code(category.getCode())
                .name(category.getName())
                .icon(category.getIcon())
                .children(category.getChildren().stream()
                        .map(this::toCategoryInfo)
                        .toList())
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

        return toDetail(spotRepository.save(spot), false);
    }

    // ── spot_requests ─────────────────────────────────────────

    /** 사용자의 스팟 등록 신청을 저장한다. */
    @Transactional
    public SpotServiceDto.SpotRequestInfo createSpotRequest(SpotServiceDto.SpotRequestCreateRequest request, Long userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        SpotRequestEntity spotRequest = SpotRequestEntity.create(
                user, request.getPlaceId(), request.getName(),
                request.getDescription(), request.getImageUrl(),
                request.getLatitude(), request.getLongitude(),
                request.getAddress(), request.getRegion(),
                request.getCity(), request.getTown(), request.getPostalCode());

        return toSpotRequestInfo(spotRequestRepository.save(spotRequest));
    }

    /**
     * 스팟 신청을 승인 또는 반려한다.
     * APPROVED: spot_requests 데이터를 기반으로 spots 테이블에 저장한다.
     * REJECTED: 반려 사유를 기록하고 상태를 변경한다.
     */
    @Transactional
    public SpotServiceDto.SpotRequestInfo reviewSpotRequest(Long requestId, Long adminId,
                                                            SpotServiceDto.SpotRequestReviewRequest request) {
        SpotRequestEntity spotRequest = spotRequestRepository.findById(requestId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SPOT_REQUEST_NOT_FOUND));
        var admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_NOT_FOUND));

        SpotRequestEntity.Status status = SpotRequestEntity.Status.valueOf(request.getStatus());

        if (status == SpotRequestEntity.Status.APPROVED) {
            com.picko.api.spot.domain.vo.Coordinate coordinate =
                    (spotRequest.getLatitude() != null && spotRequest.getLongitude() != null)
                            ? new com.picko.api.spot.domain.vo.Coordinate(spotRequest.getLatitude(), spotRequest.getLongitude())
                            : null;

            SpotAddressEntity address = SpotAddressEntity.create(
                    request.getAddressCode(),
                    spotRequest.getRegion(), spotRequest.getCity(), spotRequest.getTown(),
                    spotRequest.getPostalCode(), spotRequest.getAddress(), null,
                    coordinate);
            spotAddressRepository.save(address);

            SpotEntity spot = SpotEntity.create(
                    spotRequest.getName(), spotRequest.getDescription(),
                    spotRequest.getImageUrl(),
                    request.getIsTrending() != null ? request.getIsTrending() : false);
            spot.assignAddress(address);
            spot.registerByUser(spotRequest.getUser());
            spotRepository.save(spot);

            if (request.getCategoryIds() != null) {
                for (Long categoryId : request.getCategoryIds()) {
                    SpotCategoryEntity category = spotCategoryRepository.findById(categoryId)
                            .orElseThrow(() -> new BusinessException(ErrorCode.SPOT_CATEGORY_NOT_FOUND));
                    categoryMappingRepository.save(new SpotCategoryMappingEntity(spot, category));
                }
            }

            if (request.getHashtagIds() != null) {
                for (Long hashtagId : request.getHashtagIds()) {
                    SpotHashtagEntity hashtag = spotHashtagRepository.findById(hashtagId)
                            .orElseThrow(() -> new BusinessException(ErrorCode.SPOT_HASHTAG_NOT_FOUND));
                    hashtagMappingRepository.save(new SpotHashtagMappingEntity(spot, hashtag));
                }
            }

            spotRequest.approve(admin, spot);
        } else {
            spotRequest.reject(admin, request.getRejectReason());
        }

        return toSpotRequestInfo(spotRequest);
    }

    private SpotServiceDto.SpotRequestInfo toSpotRequestInfo(SpotRequestEntity entity) {
        return SpotServiceDto.SpotRequestInfo.builder()
                .id(entity.getId())
                .userId(entity.getUser().getId())
                .placeId(entity.getPlaceId())
                .name(entity.getName())
                .description(entity.getDescription())
                .imageUrl(entity.getImageUrl())
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .address(entity.getAddress())
                .region(entity.getRegion())
                .city(entity.getCity())
                .town(entity.getTown())
                .postalCode(entity.getPostalCode())
                .status(entity.getStatus().name())
                .rejectReason(entity.getRejectReason())
                .spotId(entity.getSpot() != null ? entity.getSpot().getId() : null)
                .createdAt(entity.getCreatedAt())
                .build();
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

    /**
     * 루트 카테고리 목록을 트리 구조로 반환한다.
     * 각 루트 카테고리에 자식 카테고리(2단계)가 포함된다.
     */
    public List<SpotServiceDto.CategoryInfo> getSpotCategories() {
        return spotCategoryRepository.findByParentIsNullOrderBySortOrderAsc()
                .stream()
                .map(this::toTreeCategoryInfo)
                .toList();
    }

    /** 새 카테고리를 생성한다. parentId가 있으면 2단계 카테고리로 생성한다. */
    @Transactional
    public SpotServiceDto.CategoryInfo createSpotCategory(SpotServiceDto.SpotCategoryRequest request) {
        SpotCategoryEntity parent = resolveParent(request.getParentId());
        SpotCategoryEntity entity = SpotCategoryEntity.create(
                request.getCode(), request.getName(), request.getIcon(), request.getSortOrder());
        if (parent != null) {
            entity.assignParent(parent);
        }
        return toCategoryInfo(spotCategoryRepository.save(entity));
    }

    /** 기존 카테고리를 수정한다. parentId 변경 시 깊이 재검증한다. */
    @Transactional
    public SpotServiceDto.CategoryInfo updateSpotCategory(Long id, SpotServiceDto.SpotCategoryRequest request) {
        SpotCategoryEntity entity = spotCategoryRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.SPOT_CATEGORY_NOT_FOUND));
        entity.update(request.getCode(), request.getName(), request.getIcon(), request.getSortOrder());
        entity.assignParent(resolveParent(request.getParentId()));
        return toCategoryInfo(entity);
    }

    /**
     * parentId 로 상위 카테고리를 조회하고 최대 2단계 깊이를 검증한다.
     * parentId == null 이면 루트 카테고리로 처리한다.
     */
    private SpotCategoryEntity resolveParent(Long parentId) {
        if (parentId == null) return null;
        SpotCategoryEntity parent = spotCategoryRepository.findById(parentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SPOT_CATEGORY_NOT_FOUND));
        if (parent.getParent() != null) {
            throw new BusinessException(ErrorCode.SPOT_CATEGORY_DEPTH_EXCEEDED);
        }
        return parent;
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
