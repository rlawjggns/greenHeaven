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

    /**
     * 사용자 주소 기반 날씨 조회
     * @param model 3일 간의 날씨정보와 주소명을 담을 모델 
     * @return 날씨 조회 페이지(View) 반환
     * @throws IOException 예외
     */
    @GetMapping("/weather")
    public String showWeather(Model model) throws IOException {
        model.addAttribute("threeDaysWeather", weatherService.getThreeDaysWeather());
        model.addAttribute("address",userService.getAddress());
        return "weather";
    }
}
