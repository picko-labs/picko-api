package com.picko.api.spot.presentation;

import com.picko.api.spot.application.SpotService;
import com.picko.api.spot.application.dto.SpotServiceDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Spot", description = "스팟 API")
@RestController
@RequestMapping("/spots")
@RequiredArgsConstructor
public class SpotController {

    private final SpotService spotService;

    @Operation(summary = "스팟 목록 조회")
    @GetMapping
    public ResponseEntity<List<SpotServiceDto.ListItem>> getSpots(
            @RequestParam(required = false) String addressCode,
            @RequestParam(required = false) Boolean isTrending,
            @RequestParam(required = false) String categoryCode,
            @RequestParam(required = false) String hashtagCode) {
        return ResponseEntity.ok(spotService.getSpots(addressCode, isTrending, categoryCode, hashtagCode));
    }

    @Operation(summary = "스팟 상세 조회")
    @GetMapping("/{id}")
    public ResponseEntity<SpotServiceDto.Detail> getSpot(@PathVariable Long id) {
        return ResponseEntity.ok(spotService.getSpot(id));
    }
}
