package com.greenheaven.backend.controller;

import com.greenheaven.backend.service.MemberService;
import com.greenheaven.backend.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
public class WeatherController {
    private final WeatherService weatherService;
    private final MemberService memberService;

    /**
     * 사용자 주소 기반 날씨 조회
     * @param model 3일 간의 날씨정보와 주소명을 담을 모델 
     * @return 날씨 조회 페이지(View) 반환
     * @throws IOException 예외
     */
    @GetMapping("/weather")
    public String showWeather(Model model) throws IOException {
        model.addAttribute("threeDaysWeather", weatherService.getThreeDaysWeather());
        model.addAttribute("address", memberService.getAuthenticatedMemberAddress());
        return "weather";
    }
}
