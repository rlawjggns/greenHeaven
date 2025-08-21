package com.greenheaven.backend.controller;

import com.greenheaven.backend.dto.CropGrowthRequestDto;
import com.greenheaven.backend.dto.CropListResponsetDto;
import com.greenheaven.backend.dto.CropRequestDto;
import com.greenheaven.backend.service.CropService;
import com.greenheaven.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/crops")
@RequiredArgsConstructor
@Slf4j
public class CropController {
    private final CropService cropService;
    private final NotificationService notificationService;

    /**
     * 작물 목록 조회
     */
    @GetMapping
    public ResponseEntity<List<CropListResponsetDto>> getCrops() {
        return ResponseEntity.ok(cropService.getCrops());
    }

    /**
     * 금일 기준 10개의 작물 목록 조회
     */
    @GetMapping("/ten")
    public ResponseEntity<List<CropListResponsetDto>> getCropsTenn() {
        return ResponseEntity.ok(cropService.getCropsTen());
    }

    /**
     * 작물 등록
     */
    @PostMapping
    public void createCrop(@RequestBody CropRequestDto request) {
        cropService.createCrop(request);
    }

    /**
     * 작물 삭제(수확)
     */
    @DeleteMapping("{cropId}")
    public void deleteCrop(@PathVariable("cropId") String cropId) {
        cropService.deleteCrop(cropId);
    }

    /**
     * 생장상태 반영
     */
    @PatchMapping("/{cropId}")
    public void updateCrop(@PathVariable("cropId") String cropId, @RequestBody CropGrowthRequestDto request) {
        cropService.updateCrop(cropId, request);
    }
}
