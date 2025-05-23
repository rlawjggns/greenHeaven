package com.greenheaven.greenheaven_app.controller;

import com.greenheaven.greenheaven_app.dto.CropRequestDto;
import com.greenheaven.greenheaven_app.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping("/notifications")
    public String getNotificationOverview(Model model) {
        model.addAttribute("notifications", notificationService.getNotificationListTen());
        return "notifications";
    }
}
