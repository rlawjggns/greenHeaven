package com.greenheaven.greenheaven_app.controller;

import com.greenheaven.greenheaven_app.domain.dto.CropListResponsetDto;
import com.greenheaven.greenheaven_app.domain.dto.CropRequestDto;
import com.greenheaven.greenheaven_app.domain.entity.Crop;
import com.greenheaven.greenheaven_app.domain.entity.CropType;
import com.greenheaven.greenheaven_app.service.CropService;
import com.greenheaven.greenheaven_app.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/crop")
@RequiredArgsConstructor
@Slf4j
public class CropController {
    private final CropService cropService;
    private final NotificationService notificationService;

    @GetMapping("/main")
    public String getMain(Model model) {
        model.addAttribute("crops", cropService.getCropListTen());
        model.addAttribute("notifications", notificationService.getNotificationListTen());
        return "crop";
    }

    @GetMapping("/registration")
    public String getRegistration() {
        return "crop_registration";
    }

    @GetMapping("/registration/{cropname}")
    public String getRegistrationDetail(@PathVariable("cropname") String cropName, Model model) {
        CropType selectedCrop = CropType.valueOf(cropName.toUpperCase()); // Enum 값으로 변환
        model.addAttribute("selectedCrop", selectedCrop);
        model.addAttribute("cropRequestDto", CropRequestDto.builder().type(selectedCrop).build());
        return "crop_registration_detail";
    }

    @PostMapping("/registration")
    public String postRegistrationDetail(@ModelAttribute CropRequestDto request) {
        cropService.createCrop(request);
        return "redirect:/crop/main";
    }

    @GetMapping("/history")
    public String getHistory() {
        return "history";
    }

    @GetMapping("/overview")
    public String getCropOverview(Model model) {
        List<CropListResponsetDto> crops = cropService.getCropListTen(); // 현재 사용자 기준 작물 리스트 가져오기
        model.addAttribute("crops", crops);
        return "crop_overview"; // Thymeleaf 파일
    }
}
