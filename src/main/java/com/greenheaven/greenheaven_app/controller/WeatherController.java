package com.greenheaven.greenheaven_app.controller;

import com.greenheaven.greenheaven_app.service.UserService;
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
    private final UserService userService;

    @GetMapping("/weather")
    public String showWeather(Model model) throws IOException {
        model.addAttribute("forecastList", weatherService.getThreeDayForecast());
        model.addAttribute("address",userService.getAddress());
        return "weather";
    }
}
