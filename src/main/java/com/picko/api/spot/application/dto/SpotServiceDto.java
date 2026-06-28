package com.picko.api.spot.application.dto;

import com.picko.api.common.exception.BusinessException;
import com.picko.api.common.exception.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class SpotServiceDto {

    @Schema(description = "스팟 목록 아이템")
    @Getter
    @Builder
    public static class ListItem {

        @Schema(description = "스팟 ID")
        private Long id;

        @Schema(description = "장소명")
        private String name;

        @Schema(description = "대표 이미지 URL")
        private String imageUrl;

        @Schema(description = "트렌딩 여부")
        private Boolean isTrending;

        @Schema(description = "위도")
        private BigDecimal latitude;

        @Schema(description = "경도")
        private BigDecimal longitude;

        @Schema(description = "카테고리 목록")
        private List<CategoryInfo> categories;

        @Schema(description = "핀 수")
        private Long pinCount;

        @Schema(description = "요청 사용자의 핀 여부 (비회원은 항상 false)")
        private Boolean isPinned;
    }

    @Schema(description = "스팟 상세")
    @Getter
    @Builder
    public static class Detail {

        @Schema(description = "스팟 ID")
        private Long id;

        @Schema(description = "장소명")
        private String name;

        @Schema(description = "장소 소개 문구")
        private String description;

        @Schema(description = "트렌딩 여부")
        private Boolean isTrending;

        @Schema(description = "대표 이미지 URL")
        private String imageUrl;

        @Schema(description = "주소 분류 정보")
        private AddressInfo spotAddress;

        @Schema(description = "카테고리 목록")
        private List<CategoryInfo> categories;

        @Schema(description = "해시태그 목록")
        private List<HashtagInfo> hashtags;

        @Schema(description = "핀 수")
        private Long pinCount;

        @Schema(description = "요청 사용자의 핀 여부 (비회원은 항상 false)")
        private Boolean isPinned;

        @Schema(description = "등록 일시")
        private LocalDateTime createdAt;
    }

    @Getter
    @Builder
    public static class CategoryInfo {
        private Long id;
        private String code;
        private String name;
        private String icon;
    }

    @Getter
    @Builder
    public static class HashtagInfo {
        private Long id;
        private String code;
        private String name;
        private String icon;
    }

    @Getter
    @Builder
    public static class AddressInfo {
        private Long id;
        private String code;
        private String region;
        private String city;
        private String town;
        private String postalCode;
        private String address;
        private String addressDetail;
        private BigDecimal latitude;
        private BigDecimal longitude;
    }

    @Schema(description = "지도 뷰포트 조회 요청 — 남서·북동 좌표로 화면 영역을 표현한다.")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ViewportRequest {

        @Schema(description = "남서(South-West) 위도", example = "37.5")
        private BigDecimal swLat;

        @Schema(description = "남서(South-West) 경도", example = "126.9")
        private BigDecimal swLng;

        @Schema(description = "북동(North-East) 위도", example = "37.6")
        private BigDecimal neLat;

        @Schema(description = "북동(North-East) 경도", example = "127.0")
        private BigDecimal neLng;

        @Schema(description = "카테고리 코드 필터 (미전달 시 전체)", example = "cafe")
        private String categoryCode;

        private static final BigDecimal LAT_MIN = new BigDecimal("-90");
        private static final BigDecimal LAT_MAX = new BigDecimal("90");
        private static final BigDecimal LNG_MIN = new BigDecimal("-180");
        private static final BigDecimal LNG_MAX = new BigDecimal("180");

        /**
         * 뷰포트 좌표를 검증·정규화해 생성한다.
         * 위경도 범위(위도 ±90·경도 ±180)와 남서≤북동 순서를 보장하며,
         * 공백 categoryCode 는 null 로 정규화해 '전체 카테고리' 의미로 통일한다.
         * 위반 시 BusinessException(INVALID_INPUT) 을 던진다.
         */
        public static ViewportRequest of(BigDecimal swLat, BigDecimal swLng,
                                         BigDecimal neLat, BigDecimal neLng, String categoryCode) {
            requireRange(swLat, LAT_MIN, LAT_MAX);
            requireRange(neLat, LAT_MIN, LAT_MAX);
            requireRange(swLng, LNG_MIN, LNG_MAX);
            requireRange(neLng, LNG_MIN, LNG_MAX);
            if (swLat.compareTo(neLat) > 0 || swLng.compareTo(neLng) > 0) {
                throw new BusinessException(ErrorCode.INVALID_INPUT);
            }
            String normalizedCategory = (categoryCode == null || categoryCode.isBlank()) ? null : categoryCode;
            return new ViewportRequest(swLat, swLng, neLat, neLng, normalizedCategory);
        }

        private static void requireRange(BigDecimal value, BigDecimal min, BigDecimal max) {
            if (value == null || value.compareTo(min) < 0 || value.compareTo(max) > 0) {
                throw new BusinessException(ErrorCode.INVALID_INPUT);
            }
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SpotCreateRequest {
        private String name;
        private Long spotAddressId;
        private String description;
        private String imageUrl;
        private Boolean isTrending;
        private Long userId;
        private Long adminId;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SpotAddressRequest {
        private String code;
        private String region;
        private String city;
        private String town;
        private String postalCode;
        private String address;
        private String addressDetail;
        private BigDecimal latitude;
        private BigDecimal longitude;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SpotCategoryRequest {
        private String code;
        private String name;
        private String icon;
        private Integer sortOrder;
    }

    @Schema(description = "스팟 등록 신청 요청")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SpotRequestCreateRequest {

        @Schema(description = "Google Maps place_id (추후 연동용)", example = "ChIJN1t_tDeuEmsRUsoyG83frY4")
        private String placeId;

        @Schema(description = "장소명", example = "성수 카페거리")
        private String name;

        @Schema(description = "장소 소개 문구")
        private String description;

        @Schema(description = "대표 이미지 URL")
        private String imageUrl;

        @Schema(description = "위도", example = "37.5447700")
        private BigDecimal latitude;

        @Schema(description = "경도", example = "127.0557800")
        private BigDecimal longitude;

        @Schema(description = "기본주소")
        private String address;

        @Schema(description = "시/도", example = "서울특별시")
        private String region;

        @Schema(description = "시/군/구", example = "성동구")
        private String city;

        @Schema(description = "읍/면/동", example = "성수동")
        private String town;

        @Schema(description = "우편번호", example = "04783")
        private String postalCode;
    }

    @Schema(description = "스팟 신청 검토 요청 (승인 / 반려)")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SpotRequestReviewRequest {

        @Schema(description = "처리 상태", example = "APPROVED or REJECTED")
        private String status;

        @Schema(description = "주소 코드 (승인 시 필수, 예: seongsu)", example = "seongsu")
        private String addressCode;

        @Schema(description = "카테고리 ID 목록 (승인 시)")
        private List<Long> categoryIds;

        @Schema(description = "해시태그 ID 목록 (승인 시)")
        private List<Long> hashtagIds;

        @Schema(description = "트렌딩 여부 (승인 시, 기본값 false)")
        private Boolean isTrending;

        @Schema(description = "반려 사유 (반려 시)")
        private String rejectReason;
    }

    @Schema(description = "트렌딩 스팟 아이템 (Nearby / Nationality 공통)")
    @Getter
    @Builder
    public static class TrendingSpotItem {

        @Schema(description = "스팟 ID")
        private Long id;

        @Schema(description = "장소명")
        private String name;

        @Schema(description = "대표 이미지 URL")
        private String imageUrl;

        @Schema(description = "트렌딩 여부")
        private Boolean isTrending;

        @Schema(description = "위도")
        private BigDecimal latitude;

        @Schema(description = "경도")
        private BigDecimal longitude;

        @Schema(description = "카테고리 목록")
        private List<CategoryInfo> categories;

        @Schema(description = "핀 수")
        private Long pinCount;

        @Schema(description = "요청 사용자의 핀 여부 (비회원은 항상 false)")
        private Boolean isPinned;

        @Schema(description = "현재 위치로부터의 거리 (km)")
        private Double distanceKm;
    }

    @Schema(description = "Trending Nearby 페이지 응답")
    @Getter
    @Builder
    public static class TrendingNearbyPage {

        @Schema(description = "스팟 목록")
        private List<TrendingSpotItem> items;

        @Schema(description = "다음 페이지 존재 여부")
        private boolean hasNext;
    }

    @Schema(description = "Trending Nationality 페이지 응답")
    @Getter
    @Builder
    public static class TrendingNationalityPage {

        @Schema(description = "스팟 목록")
        private List<TrendingSpotItem> items;

        @Schema(description = "다음 페이지 존재 여부")
        private boolean hasNext;

        @Schema(description = "적용된 국적 코드 (ISO 3166-1 alpha-2, 예: KR, US, JP)")
        private String nationality;
    }

    @Schema(description = "스팟 신청 내역")
    @Getter
    @Builder
    public static class SpotRequestInfo {

        @Schema(description = "신청 ID")
        private Long id;

        @Schema(description = "신청 사용자 ID")
        private Long userId;

        @Schema(description = "Google Maps place_id")
        private String placeId;

        @Schema(description = "장소명")
        private String name;

        @Schema(description = "장소 소개 문구")
        private String description;

        @Schema(description = "대표 이미지 URL")
        private String imageUrl;

        @Schema(description = "위도")
        private BigDecimal latitude;

        @Schema(description = "경도")
        private BigDecimal longitude;

        @Schema(description = "기본주소")
        private String address;

        @Schema(description = "시/도")
        private String region;

        @Schema(description = "시/군/구")
        private String city;

        @Schema(description = "읍/면/동")
        private String town;

        @Schema(description = "우편번호")
        private String postalCode;

        @Schema(description = "처리 상태 (PENDING | APPROVED | REJECTED)")
        private String status;

        @Schema(description = "반려 사유")
        private String rejectReason;

        @Schema(description = "승인된 스팟 ID")
        private Long spotId;

        @Schema(description = "신청 일시")
        private LocalDateTime createdAt;
    }
}
