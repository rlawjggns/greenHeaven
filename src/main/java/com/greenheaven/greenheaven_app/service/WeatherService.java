package com.greenheaven.greenheaven_app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenheaven.greenheaven_app.domain.dto.NewsDto;
import com.greenheaven.greenheaven_app.domain.dto.NewsResponse;
import com.greenheaven.greenheaven_app.domain.dto.WeatherDto;
import com.greenheaven.greenheaven_app.domain.dto.WeatherResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class WeatherService {
    @Value("${weather.serviceKey}")
    private String serviceKey;

    public List<WeatherDto> getWeather() throws IOException {
        String date = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyyMMdd")); // 현재 날짜  -1 지정
        StringBuilder urlBuilder = new StringBuilder("http://apis.data.go.kr/1360000/VilageFcstInfoService_2.0/getVilageFcst"); /*URL*/
        urlBuilder.append("?" + URLEncoder.encode("serviceKey","UTF-8") + "=" + serviceKey); /*Service Key*/
        urlBuilder.append("&" + URLEncoder.encode("pageNo","UTF-8") + "=" + URLEncoder.encode("1", "UTF-8")); /*페이지번호*/
        urlBuilder.append("&" + URLEncoder.encode("numOfRows","UTF-8") + "=" + URLEncoder.encode("10", "UTF-8")); /*한 페이지 당 결과 수*/
        urlBuilder.append("&" + URLEncoder.encode("dataType","UTF-8") + "=" + URLEncoder.encode("JSON", "UTF-8")); /*요청자료형식(XML/JSON) Default: XML*/
        urlBuilder.append("&" + URLEncoder.encode("base_date","UTF-8") + "=" + URLEncoder.encode(date, "UTF-8")); /*발표 날짜 지정(현재 날짜)*/
        urlBuilder.append("&" + URLEncoder.encode("base_time","UTF-8") + "=" + URLEncoder.encode("2300", "UTF-8")); /*23시 발표(정시단위) */
        urlBuilder.append("&" + URLEncoder.encode("nx","UTF-8") + "=" + URLEncoder.encode("55", "UTF-8")); /*예보지점의 X 좌표값*/
        urlBuilder.append("&" + URLEncoder.encode("ny","UTF-8") + "=" + URLEncoder.encode("127", "UTF-8")); /*예보지점의 Y 좌표값*/
        URL url = new URL(urlBuilder.toString()); // 요청 URL 지정
        HttpURLConnection conn = (HttpURLConnection) url.openConnection(); // URL 커넥션 설정

        conn.setRequestMethod("GET"); // URL 매서드 지정
        if(conn.getResponseCode() == 200) { // response가 200대, 즉 성공일때
            return parseWeather(conn.getInputStream());
        } else { // 실패할 경우
            throw new RuntimeException("API 응답을 읽는 데 실패했습니다.");
        }
    }

    private List<WeatherDto> parseWeather(InputStream body) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // InputStream을 NewsResponse 객체로 변환
            WeatherResponse response = objectMapper.readValue(body, WeatherResponse.class);

            // dto 리스트 반환
            return response.getItems();
        } catch (Exception e) {
            throw new RuntimeException("JSON 파싱 중 오류 발생");
        }
    }
}
