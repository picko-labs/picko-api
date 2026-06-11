package com.picko.api.pin.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class PinServiceDto {

    // ── user_pin_categories ───────────────────────────────────

    @Schema(description = "핀 카테고리 정보")
    @Getter
    @Builder
    public static class UserPinCategoryInfo {

        @Schema(description = "카테고리 ID")
        private Long id;

        @Schema(description = "사용자 ID")
        private Long userId;

        @Schema(description = "카테고리명")
        private String name;

        @Schema(description = "정렬 순서")
        private Integer sortOrder;
    }

    @Schema(description = "핀 카테고리 생성 요청")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserPinCategoryCreateRequest {

        @Schema(description = "사용자 ID")
        private Long userId;

        @Schema(description = "카테고리명")
        private String name;

        @Schema(description = "정렬 순서")
        private Integer sortOrder;
    }

    @Schema(description = "핀 카테고리 수정 요청")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserPinCategoryUpdateRequest {

        @Schema(description = "카테고리명")
        private String name;

        @Schema(description = "정렬 순서")
        private Integer sortOrder;
    }

    // ── user_pins ─────────────────────────────────────────────

    @Schema(description = "핀 정보")
    @Getter
    @Builder
    public static class UserPinInfo {

        @Schema(description = "핀 ID")
        private Long id;

        @Schema(description = "사용자 ID")
        private Long userId;

        @Schema(description = "스팟 ID")
        private Long spotId;

        @Schema(description = "핀 카테고리 ID (null이면 미분류)")
        private Long userPinCategoryId;
    }

    @Schema(description = "핀 생성 요청")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserPinCreateRequest {

        @Schema(description = "사용자 ID")
        private Long userId;

        @Schema(description = "스팟 ID")
        private Long spotId;

        @Schema(description = "핀 카테고리 ID (null이면 미분류)")
        private Long userPinCategoryId;
    }

    @Schema(description = "핀 수정 요청")
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserPinUpdateRequest {

        @Schema(description = "핀 카테고리 ID (null이면 미분류로 변경)")
        private Long userPinCategoryId;
    }
}
