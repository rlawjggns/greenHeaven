package com.greenheaven.greenheaven_app.controller;

import com.greenheaven.greenheaven_app.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class WeatherController {
    private final WeatherService weatherService;

    @GetMapping("/weather")
    public String getWeather(Model model) throws IOException {
        model.addAttribute("weatherDtos", weatherService.getWeather());
        return "weather";
    }
}
