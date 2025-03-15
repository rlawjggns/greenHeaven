package com.greenheaven.greenheaven_app.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/crop")
@RequiredArgsConstructor
@Slf4j
public class CropController {

    @GetMapping("/main")
    public String getMain() {
        return "crop";
    }

    @GetMapping("/registration")
    public String getRegistration() {
        return "crop_registration";
    }

    @GetMapping("/registration/{cropname}")
    public String getRegistrationTwo(@PathVariable("cropname") String cropName, Model model) {
        model.addAttribute("selectedCrop", cropName);
        return "crop_registration_detail";
    }

    @GetMapping("/history")
    public String getHistory() {
        return "history";
    }
}
