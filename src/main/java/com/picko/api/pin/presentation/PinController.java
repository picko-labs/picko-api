package com.picko.api.pin.presentation;

import com.picko.api.pin.application.PinService;
import com.picko.api.pin.application.dto.PinServiceDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "미연동용 | Pin")
@RestController
@RequiredArgsConstructor
public class PinController {

    private final PinService pinService;

    // ── user_pin_categories ───────────────────────────────────

    @Operation(summary = "핀 카테고리 목록 조회")
    @GetMapping("/user-pin-categories")
    public ResponseEntity<List<PinServiceDto.UserPinCategoryInfo>> getUserPinCategories(
            @RequestParam Long userId) {
        return ResponseEntity.ok(pinService.getUserPinCategories(userId));
    }

    @Operation(summary = "핀 카테고리 생성")
    @PostMapping("/user-pin-categories")
    public ResponseEntity<PinServiceDto.UserPinCategoryInfo> createUserPinCategory(
            @RequestBody PinServiceDto.UserPinCategoryCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pinService.createUserPinCategory(request));
    }

    @Operation(summary = "핀 카테고리 수정")
    @PutMapping("/user-pin-categories/{id}")
    public ResponseEntity<PinServiceDto.UserPinCategoryInfo> updateUserPinCategory(
            @PathVariable Long id,
            @RequestBody PinServiceDto.UserPinCategoryUpdateRequest request) {
        return ResponseEntity.ok(pinService.updateUserPinCategory(id, request));
    }

    @Operation(summary = "핀 카테고리 삭제")
    @DeleteMapping("/user-pin-categories/{id}")
    public ResponseEntity<Void> deleteUserPinCategory(@PathVariable Long id) {
        pinService.deleteUserPinCategory(id);
        return ResponseEntity.noContent().build();
    }

    // ── user_pins ─────────────────────────────────────────────

    @Operation(summary = "핀 목록 조회", tags = {"연동용 | Pin"})
    @GetMapping("/user-pins")
    public ResponseEntity<List<PinServiceDto.UserPinInfo>> getUserPins(
            @RequestParam Long userId) {
        return ResponseEntity.ok(pinService.getUserPins(userId));
    }

    @Operation(summary = "핀 생성", tags = {"연동용 | Pin"})
    @PostMapping("/user-pins")
    public ResponseEntity<PinServiceDto.UserPinInfo> createUserPin(
            @RequestBody PinServiceDto.UserPinCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pinService.createUserPin(request));
    }

    @Operation(summary = "핀 수정", tags = {"연동용 | Pin"})
    @PutMapping("/user-pins/{id}")
    public ResponseEntity<PinServiceDto.UserPinInfo> updateUserPin(
            @PathVariable Long id,
            @RequestBody PinServiceDto.UserPinUpdateRequest request) {
        return ResponseEntity.ok(pinService.updateUserPin(id, request));
    }

    @Operation(summary = "핀 삭제", tags = {"연동용 | Pin"})
    @DeleteMapping("/user-pins/{id}")
    public ResponseEntity<Void> deleteUserPin(@PathVariable Long id) {
        pinService.deleteUserPin(id);
        return ResponseEntity.noContent().build();
    }
}
