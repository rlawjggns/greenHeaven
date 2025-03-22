package com.greenheaven.greenheaven_app.controller;

import com.greenheaven.greenheaven_app.domain.dto.DailyForecast;
import com.greenheaven.greenheaven_app.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class WeatherController {
    private final WeatherService weatherService;

    @GetMapping("/weather")
    public String showWeather(Model model) {
        List<DailyForecast> forecastList = weatherService.getThreeDayForecast();
        model.addAttribute("forecastList", forecastList);
        return "weather";
    }

    @GetMapping("/weather/create")
    public void createWeather() throws IOException {
        weatherService.createWeather();
    }
}
