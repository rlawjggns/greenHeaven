package com.greenheaven.greenheaven_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SubscriptionController {

    @GetMapping("/subscription")
    public String get() {
        return "subscription";
    }
}
