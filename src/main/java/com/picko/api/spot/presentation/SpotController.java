package com.picko.api.spot.presentation;

import com.picko.api.common.response.ApiResponse;
import com.picko.api.common.security.CurrentUserId;
import com.picko.api.spot.application.SpotService;
import com.picko.api.spot.application.dto.SpotServiceDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.web.bind.annotation.PatchMapping;

@Tag(name = "미연동용 | Spot")
@RestController
@RequiredArgsConstructor
public class SpotController {

    private final SpotService spotService;

    // ── 스팟 ──────────────────────────────────────────────────

    @Operation(summary = "스팟 목록 조회", description = "비회원 호출 가능. 회원이면 isPinned 개인화 포함", tags = {"연동용 | Spot"})
    @GetMapping("/spots")
    public ResponseEntity<ApiResponse<List<SpotServiceDto.ListItem>>> getSpots(
            @RequestParam BigDecimal swLat,
            @RequestParam BigDecimal swLng,
            @RequestParam BigDecimal neLat,
            @RequestParam BigDecimal neLng,
            @RequestParam(required = false) String categoryCode,
            @CurrentUserId Long userId) {
        SpotServiceDto.ViewportRequest request =
                SpotServiceDto.ViewportRequest.of(swLat, swLng, neLat, neLng, categoryCode);
        return ResponseEntity.ok(ApiResponse.success(spotService.getSpots(request, userId)));
    }

    @Operation(summary = "스팟 상세 조회", description = "비회원 호출 가능. 회원이면 isPinned 개인화 포함", tags = {"연동용 | Spot"})
    @GetMapping("/spots/{id}")
    public ResponseEntity<ApiResponse<SpotServiceDto.Detail>> getSpot(
            @PathVariable Long id,
            @CurrentUserId Long userId) {
        return ResponseEntity.ok(ApiResponse.success(spotService.getSpot(id, userId)));
    }

    @Operation(summary = "스팟 저장", tags = {"연동용 | Spot"})
    @PostMapping("/spots")
    public ResponseEntity<ApiResponse<SpotServiceDto.Detail>> createSpot(
            @RequestBody SpotServiceDto.SpotCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(spotService.createSpot(request)));
    }

    // ── 스팟 신청 ─────────────────────────────────────────────

    @Operation(summary = "스팟 등록 신청", description = "회원 전용. 어드민 승인 후 spots에 저장됨", tags = {"연동용 | Spot"})
    @PostMapping("/spot-requests")
    public ResponseEntity<ApiResponse<SpotServiceDto.SpotRequestInfo>> createSpotRequest(
            @RequestBody SpotServiceDto.SpotRequestCreateRequest request,
            @CurrentUserId Long userId) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(spotService.createSpotRequest(request, userId)));
    }

    @Operation(summary = "스팟 신청 검토", description = "어드민 전용. status: APPROVED or REJECTED")
    @PatchMapping("/spot-requests/{id}")
    public ResponseEntity<ApiResponse<SpotServiceDto.SpotRequestInfo>> reviewSpotRequest(
            @PathVariable Long id,
            @RequestBody SpotServiceDto.SpotRequestReviewRequest request,
            @CurrentUserId Long adminId) {
        return ResponseEntity.ok(ApiResponse.success(spotService.reviewSpotRequest(id, adminId, request)));
    }

    // ── 주소 ──────────────────────────────────────────────────

    @Operation(summary = "주소 목록 조회", tags = {"미연동용 | Spot Addresses"})
    @GetMapping("/spot-addresses")
    public ResponseEntity<ApiResponse<List<SpotServiceDto.AddressInfo>>> getSpotAddresses() {
        return ResponseEntity.ok(ApiResponse.success(spotService.getSpotAddresses()));
    }

    @Operation(summary = "주소 생성", tags = {"미연동용 | Spot Addresses"})
    @PostMapping("/spot-addresses")
    public ResponseEntity<ApiResponse<SpotServiceDto.AddressInfo>> createSpotAddress(
            @RequestBody SpotServiceDto.SpotAddressRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(spotService.createSpotAddress(request)));
    }

    @Operation(summary = "주소 수정", tags = {"미연동용 | Spot Addresses"})
    @PutMapping("/spot-addresses/{id}")
    public ResponseEntity<ApiResponse<SpotServiceDto.AddressInfo>> updateSpotAddress(
            @PathVariable Long id,
            @RequestBody SpotServiceDto.SpotAddressRequest request) {
        return ResponseEntity.ok(ApiResponse.success(spotService.updateSpotAddress(id, request)));
    }

    @Operation(summary = "주소 삭제", tags = {"미연동용 | Spot Addresses"})
    @DeleteMapping("/spot-addresses/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSpotAddress(@PathVariable Long id) {
        spotService.deleteSpotAddress(id);
        return ResponseEntity.ok(ApiResponse.<Void>success(null));
    }

    // ── 카테고리 ──────────────────────────────────────────────

    @Operation(summary = "카테고리 목록 조회", tags = {"연동용 | Spot Categories"})
    @GetMapping("/spot-categories")
    public ResponseEntity<ApiResponse<List<SpotServiceDto.CategoryInfo>>> getSpotCategories() {
        return ResponseEntity.ok(ApiResponse.success(spotService.getSpotCategories()));
    }

    @Operation(summary = "카테고리 생성", tags = {"미연동용 | Spot Categories"})
    @PostMapping("/spot-categories")
    public ResponseEntity<ApiResponse<SpotServiceDto.CategoryInfo>> createSpotCategory(
            @RequestBody SpotServiceDto.SpotCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(spotService.createSpotCategory(request)));
    }

    @Operation(summary = "카테고리 수정", tags = {"미연동용 | Spot Categories"})
    @PutMapping("/spot-categories/{id}")
    public ResponseEntity<ApiResponse<SpotServiceDto.CategoryInfo>> updateSpotCategory(
            @PathVariable Long id,
            @RequestBody SpotServiceDto.SpotCategoryRequest request) {
        return ResponseEntity.ok(ApiResponse.success(spotService.updateSpotCategory(id, request)));
    }

    @Operation(summary = "카테고리 삭제", tags = {"미연동용 | Spot Categories"})
    @DeleteMapping("/spot-categories/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSpotCategory(@PathVariable Long id) {
        spotService.deleteSpotCategory(id);
        return ResponseEntity.ok(ApiResponse.<Void>success(null));
    }

    // ── 카테고리 매핑 ─────────────────────────────────────────

    @Operation(summary = "스팟에 카테고리 연결", tags = {"미연동용 | Spot Categories"})
    @PostMapping("/spots/{spotId}/categories/{categoryId}")
    public ResponseEntity<ApiResponse<Void>> addCategoryToSpot(
            @PathVariable Long spotId,
            @PathVariable Long categoryId) {
        spotService.addCategoryToSpot(spotId, categoryId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<Void>success(null));
    }

    @Operation(summary = "스팟에서 카테고리 연결 해제", tags = {"미연동용 | Spot Categories"})
    @DeleteMapping("/spots/{spotId}/categories/{categoryId}")
    public ResponseEntity<ApiResponse<Void>> removeCategoryFromSpot(
            @PathVariable Long spotId,
            @PathVariable Long categoryId) {
        spotService.removeCategoryFromSpot(spotId, categoryId);
        return ResponseEntity.ok(ApiResponse.<Void>success(null));
    }
}
