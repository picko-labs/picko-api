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

import java.util.List;

@Tag(name = "Pin", description = "핀 API (내부용)")
@RestController
@RequiredArgsConstructor
public class PinController {

    private final PinService pinService;

    // ── user_pin_categories ───────────────────────────────────

    @Operation(summary = "핀 카테고리 목록 조회", description = "내부용")
    @GetMapping("/user-pin-categories")
    public ResponseEntity<ApiResponse<List<PinServiceDto.UserPinCategoryInfo>>> getUserPinCategories(
            @CurrentUserId Long userId) {
        return ResponseEntity.ok(ApiResponse.success(pinService.getUserPinCategories(userId)));
    }

    @Operation(summary = "핀 카테고리 생성", description = "내부용")
    @PostMapping("/user-pin-categories")
    public ResponseEntity<ApiResponse<PinServiceDto.UserPinCategoryInfo>> createUserPinCategory(
            @RequestBody PinServiceDto.UserPinCategoryCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(pinService.createUserPinCategory(request)));
    }

    @Operation(summary = "핀 카테고리 수정", description = "내부용")
    @PutMapping("/user-pin-categories/{id}")
    public ResponseEntity<ApiResponse<PinServiceDto.UserPinCategoryInfo>> updateUserPinCategory(
            @PathVariable Long id,
            @RequestBody PinServiceDto.UserPinCategoryUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(pinService.updateUserPinCategory(id, request)));
    }

    @Operation(summary = "핀 카테고리 삭제", description = "내부용")
    @DeleteMapping("/user-pin-categories/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUserPinCategory(@PathVariable Long id) {
        pinService.deleteUserPinCategory(id);
        return ResponseEntity.ok(ApiResponse.<Void>success(null));
    }

    // ── user_pins ─────────────────────────────────────────────

    @Operation(summary = "핀 목록 조회", description = "내부용")
    @GetMapping("/user-pins")
    public ResponseEntity<ApiResponse<List<PinServiceDto.UserPinInfo>>> getUserPins(
            @CurrentUserId Long userId) {
        return ResponseEntity.ok(ApiResponse.success(pinService.getUserPins(userId)));
    }

    @Operation(summary = "핀 생성", description = "내부용")
    @PostMapping("/user-pins")
    public ResponseEntity<ApiResponse<PinServiceDto.UserPinInfo>> createUserPin(
            @RequestBody PinServiceDto.UserPinCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(pinService.createUserPin(request)));
    }

    @Operation(summary = "핀 수정", description = "내부용")
    @PutMapping("/user-pins/{id}")
    public ResponseEntity<ApiResponse<PinServiceDto.UserPinInfo>> updateUserPin(
            @PathVariable Long id,
            @RequestBody PinServiceDto.UserPinUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.success(pinService.updateUserPin(id, request)));
    }

    @Operation(summary = "핀 삭제", description = "내부용")
    @DeleteMapping("/user-pins/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUserPin(@PathVariable Long id) {
        pinService.deleteUserPin(id);
        return ResponseEntity.ok(ApiResponse.<Void>success(null));
    }
}
