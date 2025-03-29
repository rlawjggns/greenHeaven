package com.greenheaven.greenheaven_app.domain.dto;

import com.greenheaven.greenheaven_app.domain.entity.Weather;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@AllArgsConstructor
public class DailyWeather {
    private LocalDate date;
    private Weather representative; // 예: 정오 또는 첫 번째 측정값
    private Integer minTemp;         // 해당 날짜의 최저 기온
    private Integer maxTemp;         // 해당 날짜의 최고 기온
    private List<Weather> measurements;  // 해당 날짜의 모든 Weather 데이터
}
