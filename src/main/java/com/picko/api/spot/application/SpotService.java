package com.picko.api.spot.application;

import com.picko.api.pin.infrastructure.UserPinRepository;
import com.picko.api.spot.application.dto.SpotServiceDto;
import com.picko.api.spot.domain.SpotAddressEntity;
import com.picko.api.spot.domain.SpotCategoryEntity;
import com.picko.api.spot.domain.SpotEntity;
import com.picko.api.spot.domain.SpotHashtagEntity;
import com.picko.api.spot.infrastructure.SpotCategoryMappingRepository;
import com.picko.api.spot.infrastructure.SpotHashtagMappingRepository;
import com.picko.api.spot.infrastructure.SpotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SpotService {

    private final SpotRepository spotRepository;
    private final SpotCategoryMappingRepository categoryMappingRepository;
    private final SpotHashtagMappingRepository hashtagMappingRepository;
    private final UserPinRepository userPinRepository;

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
     * 존재하지 않는 id면 IllegalArgumentException을 던진다.
     */
    public SpotServiceDto.Detail getSpot(Long id) {
        SpotEntity spot = spotRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Spot not found: " + id));
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
