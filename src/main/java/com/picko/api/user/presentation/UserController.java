package com.picko.api.user.presentation;

import com.picko.api.user.application.UserService;
import com.picko.api.user.application.dto.UserServiceDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "User", description = "사용자 계정 관리 API")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(summary = "사용자 목록 조회")
    @GetMapping
    public ResponseEntity<List<UserServiceDto.Response>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @Operation(summary = "사용자 단건 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "사용자 없음")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserServiceDto.Response> getUser(
            @Parameter(description = "사용자 ID") @PathVariable Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @Operation(summary = "사용자 등록")
    @ApiResponse(responseCode = "200", description = "등록 성공")
    @PostMapping
    public ResponseEntity<UserServiceDto.Response> createUser(@RequestBody UserServiceDto.Request request) {
        return ResponseEntity.ok(userService.createUser(request));
    }

    @Operation(summary = "사용자 정보 수정")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "수정 성공"),
            @ApiResponse(responseCode = "404", description = "사용자 없음")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserServiceDto.Response> updateUser(
            @Parameter(description = "사용자 ID") @PathVariable Long id,
            @RequestBody UserServiceDto.Request request) {
        return ResponseEntity.ok(userService.updateUser(id, request));
    }

    @Operation(summary = "사용자 삭제")
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "사용자 ID") @PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
