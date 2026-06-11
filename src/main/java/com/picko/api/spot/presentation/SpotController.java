package com.picko.api.spot.presentation;

import com.picko.api.spot.application.SpotService;
import com.picko.api.spot.application.dto.SpotServiceDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Spot", description = "스팟 API")
@RestController
@RequiredArgsConstructor
public class SpotController {

    private final SpotService spotService;

    // ── 스팟 ──────────────────────────────────────────────────

    @Operation(summary = "스팟 목록 조회", description = "연동용")
    @GetMapping("/spots")
    public ResponseEntity<List<SpotServiceDto.ListItem>> getSpots(
            @RequestParam(required = false) String addressCode,
            @RequestParam(required = false) Boolean isTrending,
            @RequestParam(required = false) String categoryCode,
            @RequestParam(required = false) String hashtagCode) {
        return ResponseEntity.ok(spotService.getSpots(addressCode, isTrending, categoryCode, hashtagCode));
    }

    @Operation(summary = "스팟 상세 조회", description = "연동용")
    @GetMapping("/spots/{id}")
    public ResponseEntity<SpotServiceDto.Detail> getSpot(@PathVariable Long id) {
        return ResponseEntity.ok(spotService.getSpot(id));
    }

    @Operation(summary = "스팟 저장", description = "연동용")
    @PostMapping("/spots")
    public ResponseEntity<SpotServiceDto.Detail> createSpot(
            @RequestBody SpotServiceDto.SpotCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(spotService.createSpot(request));
    }

    // ── 주소 ──────────────────────────────────────────────────

    @Operation(summary = "주소 목록 조회", description = "내부용")
    @GetMapping("/spot-addresses")
    public ResponseEntity<List<SpotServiceDto.AddressInfo>> getSpotAddresses() {
        return ResponseEntity.ok(spotService.getSpotAddresses());
    }

    @Operation(summary = "주소 생성", description = "내부용")
    @PostMapping("/spot-addresses")
    public ResponseEntity<SpotServiceDto.AddressInfo> createSpotAddress(
            @RequestBody SpotServiceDto.SpotAddressRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(spotService.createSpotAddress(request));
    }

    @Operation(summary = "주소 수정", description = "내부용")
    @PutMapping("/spot-addresses/{id}")
    public ResponseEntity<SpotServiceDto.AddressInfo> updateSpotAddress(
            @PathVariable Long id,
            @RequestBody SpotServiceDto.SpotAddressRequest request) {
        return ResponseEntity.ok(spotService.updateSpotAddress(id, request));
    }

    @Operation(summary = "주소 삭제", description = "내부용")
    @DeleteMapping("/spot-addresses/{id}")
    public ResponseEntity<Void> deleteSpotAddress(@PathVariable Long id) {
        spotService.deleteSpotAddress(id);
        return ResponseEntity.noContent().build();
    }

    // ── 카테고리 ──────────────────────────────────────────────

    @Operation(summary = "카테고리 목록 조회", description = "내부용")
    @GetMapping("/spot-categories")
    public ResponseEntity<List<SpotServiceDto.CategoryInfo>> getSpotCategories() {
        return ResponseEntity.ok(spotService.getSpotCategories());
    }

    @Operation(summary = "카테고리 생성", description = "내부용")
    @PostMapping("/spot-categories")
    public ResponseEntity<SpotServiceDto.CategoryInfo> createSpotCategory(
            @RequestBody SpotServiceDto.SpotCategoryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(spotService.createSpotCategory(request));
    }

    @Operation(summary = "카테고리 수정", description = "내부용")
    @PutMapping("/spot-categories/{id}")
    public ResponseEntity<SpotServiceDto.CategoryInfo> updateSpotCategory(
            @PathVariable Long id,
            @RequestBody SpotServiceDto.SpotCategoryRequest request) {
        return ResponseEntity.ok(spotService.updateSpotCategory(id, request));
    }

    @Operation(summary = "카테고리 삭제", description = "내부용")
    @DeleteMapping("/spot-categories/{id}")
    public ResponseEntity<Void> deleteSpotCategory(@PathVariable Long id) {
        spotService.deleteSpotCategory(id);
        return ResponseEntity.noContent().build();
    }

    // ── 카테고리 매핑 ─────────────────────────────────────────

    @Operation(summary = "스팟에 카테고리 연결", description = "내부용")
    @PostMapping("/spots/{spotId}/categories/{categoryId}")
    public ResponseEntity<Void> addCategoryToSpot(
            @PathVariable Long spotId,
            @PathVariable Long categoryId) {
        spotService.addCategoryToSpot(spotId, categoryId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "스팟에서 카테고리 연결 해제", description = "내부용")
    @DeleteMapping("/spots/{spotId}/categories/{categoryId}")
    public ResponseEntity<Void> removeCategoryFromSpot(
            @PathVariable Long spotId,
            @PathVariable Long categoryId) {
        spotService.removeCategoryFromSpot(spotId, categoryId);
        return ResponseEntity.noContent().build();
    }
}
