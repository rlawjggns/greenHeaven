package com.greenheaven.backend.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InformationController {

    @GetMapping("/information")
    public String getInformation(Model model) {
        return "information";
    }
}
