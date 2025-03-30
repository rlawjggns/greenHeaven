package com.greenheaven.greenheaven_app.controller;

import com.greenheaven.greenheaven_app.service.CropService;
import com.greenheaven.greenheaven_app.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/board")
@RequiredArgsConstructor
@Slf4j
public class BoardController {
    private final CropService cropService;
    private final NotificationService notificationService;

    @GetMapping("")
    public String getBoard() {
        return "board";
    }

}
