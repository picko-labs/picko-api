package com.picko.api.spot.presentation;

import com.picko.api.common.response.ApiResponse;
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

@Tag(name = "Spot", description = "스팟 API")
@RestController
@RequiredArgsConstructor
public class SpotController {

    private final SpotService spotService;

    // ── 스팟 ──────────────────────────────────────────────────

    @Operation(summary = "스팟 목록 조회", description = "연동용")
    @GetMapping("/spots")
    public ResponseEntity<ApiResponse<List<SpotServiceDto.ListItem>>> getSpots(
            @RequestParam BigDecimal swLat,
            @RequestParam BigDecimal swLng,
            @RequestParam BigDecimal neLat,
            @RequestParam BigDecimal neLng,
            @RequestParam(required = false) String categoryCode) {
        SpotServiceDto.ViewportRequest request =
                SpotServiceDto.ViewportRequest.of(swLat, swLng, neLat, neLng, categoryCode);
        return ResponseEntity.ok(ApiResponse.success(spotService.getSpots(request)));
    }

    @Operation(summary = "스팟 상세 조회", description = "연동용")
    @GetMapping("/spots/{id}")
    public ResponseEntity<ApiResponse<SpotServiceDto.Detail>> getSpot(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(spotService.getSpot(id)));
    }

    @Operation(summary = "스팟 저장", description = "연동용")
    @PostMapping("/spots")
    public ResponseEntity<ApiResponse<SpotServiceDto.Detail>> createSpot(
            @RequestBody SpotServiceDto.SpotCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(spotService.createSpot(request)));
    }

    // ── 주소 ──────────────────────────────────────────────────

    @Operation(summary = "주소 목록 조회", description = "내부용")
    @GetMapping("/spot-addresses")
    public ResponseEntity<ApiResponse<List<SpotServiceDto.AddressInfo>>> getSpotAddresses() {
        return ResponseEntity.ok(ApiResponse.success(spotService.getSpotAddresses()));
    }

    @Operation(summary = "주소 생성", description = "내부용")
    @PostMapping("/spot-addresses")
    public ResponseEntity<ApiResponse<SpotServiceDto.AddressInfo>> createSpotAddress(
            @RequestBody SpotServiceDto.SpotAddressRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(spotService.createSpotAddress(request)));
    }

    @Operation(summary = "주소 수정", description = "내부용")
    @PutMapping("/spot-addresses/{id}")
    public ResponseEntity<ApiResponse<SpotServiceDto.AddressInfo>> updateSpotAddress(
            @PathVariable Long id,
            @RequestBody SpotServiceDto.SpotAddressRequest request) {
        return ResponseEntity.ok(ApiResponse.success(spotService.updateSpotAddress(id, request)));
    }

    @Operation(summary = "주소 삭제", description = "내부용")
    @DeleteMapping("/spot-addresses/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSpotAddress(@PathVariable Long id) {
        spotService.deleteSpotAddress(id);
        return ResponseEntity.ok(ApiResponse.<Void>success(null));
    }

    // ── 카테고리 ──────────────────────────────────────────────

    @Operation(summary = "카테고리 목록 조회", description = "내부용")
    @GetMapping("/spot-categories")
    public ResponseEntity<ApiResponse<List<SpotServiceDto.CategoryInfo>>> getSpotCategories() {
        return ResponseEntity.ok(ApiResponse.success(spotService.getSpotCategories()));
    }

    @Operation(summary = "카테고리 생성", description = "내부용")
    @PostMapping("/spot-categories")
    public ResponseEntity<ApiResponse<SpotServiceDto.CategoryInfo>> createSpotCategory(
            @RequestBody SpotServiceDto.SpotCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(spotService.createSpotCategory(request)));
    }

    @Operation(summary = "카테고리 수정", description = "내부용")
    @PutMapping("/spot-categories/{id}")
    public ResponseEntity<ApiResponse<SpotServiceDto.CategoryInfo>> updateSpotCategory(
            @PathVariable Long id,
            @RequestBody SpotServiceDto.SpotCategoryRequest request) {
        return ResponseEntity.ok(ApiResponse.success(spotService.updateSpotCategory(id, request)));
    }

    @Operation(summary = "카테고리 삭제", description = "내부용")
    @DeleteMapping("/spot-categories/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSpotCategory(@PathVariable Long id) {
        spotService.deleteSpotCategory(id);
        return ResponseEntity.ok(ApiResponse.<Void>success(null));
    }

    // ── 카테고리 매핑 ─────────────────────────────────────────

    @Operation(summary = "스팟에 카테고리 연결", description = "내부용")
    @PostMapping("/spots/{spotId}/categories/{categoryId}")
    public ResponseEntity<ApiResponse<Void>> addCategoryToSpot(
            @PathVariable Long spotId,
            @PathVariable Long categoryId) {
        spotService.addCategoryToSpot(spotId, categoryId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<Void>success(null));
    }

    @Operation(summary = "스팟에서 카테고리 연결 해제", description = "내부용")
    @DeleteMapping("/spots/{spotId}/categories/{categoryId}")
    public ResponseEntity<ApiResponse<Void>> removeCategoryFromSpot(
            @PathVariable Long spotId,
            @PathVariable Long categoryId) {
        spotService.removeCategoryFromSpot(spotId, categoryId);
        return ResponseEntity.ok(ApiResponse.<Void>success(null));
    }
}
