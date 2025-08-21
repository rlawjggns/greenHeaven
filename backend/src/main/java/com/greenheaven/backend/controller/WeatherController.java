package com.greenheaven.backend.controller;

import com.greenheaven.backend.dto.WeatherResponseDto;
import com.greenheaven.backend.service.MemberService;
import com.greenheaven.backend.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping ("/api/weather")
@RequiredArgsConstructor
public class WeatherController {
    private final WeatherService weatherService;
    private final MemberService memberService;

    /**
     * 사용자 주소 기반 날씨 조회
     * @return 날씨 조회 페이지(View) 반환
     * @throws IOException 예외
     */
    @GetMapping
    public ResponseEntity<WeatherResponseDto> showWeather( ) throws IOException {
        return ResponseEntity.ok(weatherService.getThreeDaysWeather());
    }
}
