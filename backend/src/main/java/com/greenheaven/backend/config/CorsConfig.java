package com.greenheaven.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true); // 쿠키 전송 허용
        config.setAllowedOrigins(Arrays.asList("http://localhost:1272")); // 허용할 프론트 주소
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH")); // 허용 HTTP 메서드
        config.setAllowedHeaders(Arrays.asList("*")); // 허용 헤더

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config); // 모든 URL에 적용

        return new CorsFilter(source);
    }
}