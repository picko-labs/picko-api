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

    public List<SpotServiceDto.ListItem> getSpots(
            String addressCode, Boolean isTrending, String categoryCode, String hashtagCode) {
        return spotRepository.findByFilters(addressCode, isTrending, categoryCode, hashtagCode)
                .stream()
                .map(this::toListItem)
                .toList();
    }

    public SpotServiceDto.Detail getSpot(Long id) {
        SpotEntity spot = spotRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Spot not found: " + id));
        return toDetail(spot);
    }

    private SpotServiceDto.ListItem toListItem(SpotEntity spot) {
        List<SpotServiceDto.CategoryInfo> categories = categoryMappingRepository
                .findByIdSpotId(spot.getId())
                .stream()
                .map(m -> toCategoryInfo(m.getSpotCategory()))
                .toList();

        return SpotServiceDto.ListItem.builder()
                .id(spot.getId())
                .name(spot.getName())
                .location(spot.getLocation())
                .imageUrl(spot.getImageUrl())
                .isTrending(spot.getIsTrending())
                .latitude(spot.getCoordinate() != null ? spot.getCoordinate().getLatitude() : null)
                .longitude(spot.getCoordinate() != null ? spot.getCoordinate().getLongitude() : null)
                .categories(categories)
                .pinCount(userPinRepository.countBySpotIdAndDeletedAtIsNull(spot.getId()))
                .build();
    }

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
                .location(spot.getLocation())
                .address(spot.getAddress())
                .description(spot.getDescription())
                .isTrending(spot.getIsTrending())
                .latitude(spot.getCoordinate() != null ? spot.getCoordinate().getLatitude() : null)
                .longitude(spot.getCoordinate() != null ? spot.getCoordinate().getLongitude() : null)
                .imageUrl(spot.getImageUrl())
                .spotAddress(spot.getSpotAddress() != null ? toAddressInfo(spot.getSpotAddress()) : null)
                .categories(categories)
                .hashtags(hashtags)
                .pinCount(userPinRepository.countBySpotIdAndDeletedAtIsNull(spot.getId()))
                .createdAt(spot.getCreatedAt())
                .build();
    }

    private SpotServiceDto.CategoryInfo toCategoryInfo(SpotCategoryEntity category) {
        return SpotServiceDto.CategoryInfo.builder()
                .id(category.getId())
                .code(category.getCode())
                .name(category.getName())
                .icon(category.getIcon())
                .build();
    }

    private SpotServiceDto.HashtagInfo toHashtagInfo(SpotHashtagEntity hashtag) {
        return SpotServiceDto.HashtagInfo.builder()
                .id(hashtag.getId())
                .code(hashtag.getCode())
                .name(hashtag.getName())
                .icon(hashtag.getIcon())
                .build();
    }

    private SpotServiceDto.AddressInfo toAddressInfo(SpotAddressEntity address) {
        return SpotServiceDto.AddressInfo.builder()
                .id(address.getId())
                .code(address.getCode())
                .name(address.getName())
                .nameEn(address.getNameEn())
                .build();
    }
}
