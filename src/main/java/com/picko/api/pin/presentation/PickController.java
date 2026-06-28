package com.picko.api.pin.presentation;

import com.picko.api.common.response.ApiResponse;
import com.picko.api.common.security.CurrentUserId;
import com.picko.api.pin.application.PinService;
import com.picko.api.pin.application.dto.PinServiceDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Tag(name = "Pick", description = "Pick API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/pick")
public class PickController {

    private final PinService pinService;

    @Operation(summary = "내 핀 카테고리 목록 조회", description = "회원 전용. 카테고리별 핀된 스팟 수(spotCount) 포함")
    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<PinServiceDto.UserPinCategoryInfo>>> getPickCategories(
            @CurrentUserId Long userId) {
        return ResponseEntity.ok(ApiResponse.success(pinService.getPickCategories(userId)));
    }

    @Operation(summary = "내 핀 카테고리 추가", description = "회원 전용. sortOrder는 기존 카테고리 수로 자동 설정")
    @PostMapping("/categories")
    public ResponseEntity<ApiResponse<PinServiceDto.UserPinCategoryInfo>> createPickCategory(
            @RequestBody PinServiceDto.PickCategoryCreateRequest request,
            @CurrentUserId Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(pinService.createPickCategory(userId, request)));
    }

    @Operation(summary = "뷰포트 내 내 핀 스팟 조회", description = "회원 전용. categoryId 없으면 전체 핀 기준으로 반환")
    @GetMapping("/spots")
    public ResponseEntity<ApiResponse<List<PinServiceDto.PickSpotItem>>> getPickSpots(
            @RequestParam BigDecimal swLat,
            @RequestParam BigDecimal swLng,
            @RequestParam BigDecimal neLat,
            @RequestParam BigDecimal neLng,
            @RequestParam(required = false) Long categoryId,
            @CurrentUserId Long userId) {
        return ResponseEntity.ok(ApiResponse.success(
                pinService.getPickSpots(userId, swLat, swLng, neLat, neLng, categoryId)));
    }
}
