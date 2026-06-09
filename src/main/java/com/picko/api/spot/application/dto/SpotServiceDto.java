package com.picko.api.spot.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

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
}
