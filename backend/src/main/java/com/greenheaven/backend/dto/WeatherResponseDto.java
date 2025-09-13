package com.greenheaven.backend.dto;


import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class WeatherResponseDto {
    private String address;
    private List<DailyWeather> weathers;
}
