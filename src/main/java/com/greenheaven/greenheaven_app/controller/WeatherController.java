package com.greenheaven.greenheaven_app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class WeatherController {

    @GetMapping("/weather")
    public String getWeather() {
        return "weather";
    }
}
