package com.greenheaven.backend.controller;

import com.greenheaven.backend.dto.CropGrowthDto;
import com.greenheaven.backend.dto.CropListResponsetDto;
import com.greenheaven.backend.dto.CropRequestDto;
import com.greenheaven.backend.domain.CropType;
import com.greenheaven.backend.service.CropService;
import com.greenheaven.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class CropController {
    private final CropService cropService;
    private final NotificationService notificationService;

//    테스트 주석추가
    /**
     * 작물관리 메인 페이지
     */
    @GetMapping("/crop")
    public String getMain(Model model) {
        model.addAttribute("crops", cropService.getCropListTen());
        model.addAttribute("notifications", notificationService.getNotificationListTen());
        return "crop";
    }

    /**
     * 작물 등록 페이지 1 - 종류 선택
     */
    @GetMapping("/crop/registration")
    public String getRegistration() {
        return "crop_registration";
    }

    /**
     * 작물 등록 페이지 2 - 상세 정보 선택
     */
    @GetMapping("/crop/registration/{cropname}")
    public String getRegistrationDetail(@PathVariable("cropname") String cropName, Model model) {
        CropType selectedCrop = CropType.valueOf(cropName.toUpperCase()); // Enum 값으로 변환
        model.addAttribute("selectedCrop", selectedCrop);
        model.addAttribute("cropRequestDto", CropRequestDto.builder().type(selectedCrop).build());
        return "crop_registration_detail";
    }

    /**
     * 작물 등록
     */
    @PostMapping("/crop")
    public String postRegistrationDetail(@ModelAttribute CropRequestDto request) {
        cropService.createCrop(request);
        return "redirect:/crop";
    }

    /**
     * 작물 수확/삭제
     */
    @GetMapping("/crop/delete/{id}")
    public String deleteCrop(@PathVariable("id") String cropId) {
        cropService.deleteCrop(cropId);
        return "redirect:/crop";
    }

    /**
     * 작물 목록 페이지
     */
    @GetMapping("/crops")
    public String getCropOverview(Model model) {
        List<CropListResponsetDto> crops = cropService.getCropListTen(); // 현재 사용자 기준 작물 리스트 가져오기
        model.addAttribute("crops", crops);
        return "crops"; // Thymeleaf 파일
    }

    /**
     * 생장상태 관리 페이지
     */
    @GetMapping("/crop/growth")
    public String cropGrowthPage(Model model) {

        model.addAttribute("crops", cropService.getCrops());
        model.addAttribute("cropGrowthDto", new CropGrowthDto());
        return "crop_growth";
    }

    /**
     * 생장상태 반영
     */
    @PostMapping("/crop/growth")
    public String cropGrowth(@ModelAttribute CropGrowthDto request) {
        cropService.cropGrowth(request);
        return "redirect:/crop";
    }
}
