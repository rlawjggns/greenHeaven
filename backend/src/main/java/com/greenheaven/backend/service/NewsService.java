package com.greenheaven.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenheaven.backend.dto.NewsDto;
import com.greenheaven.backend.dto.NewsResponse;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class NewsService {
    @Value("${naver.id}")
    private String id;

    @Value("${naver.secret}")
    private String secret;

    /**
     * 뉴스 정보 생성
     * 특정 키워드를 기준으로 네이버 뉴스 검색 API를 호출하여
     * @return 뉴스 객체 리스트 반환
     */
    public List<NewsDto> callNewsAPI() {
        String keyword = URLEncoder.encode("농업", StandardCharsets.UTF_8); // 농업 키워드 설정
        String apiURL = "https://openapi.naver.com/v1/search/news.json?query=" + keyword + "&display=10"; // 요청 URL 설정

        Map<String, String> headers = Map.of(
                "X-Naver-Client-Id", id,   // 아이디 값
                "X-Naver-Client-Secret", secret // 시크릿 값
        );

        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(apiURL).openConnection(); // URL 연결
            conn.setRequestMethod("GET"); // GET 메서드 설정

            headers.forEach(conn::setRequestProperty); // 헤더 설정

            int responseCode = conn.getResponseCode(); // 응답 코드 확인
            if (responseCode == HttpURLConnection.HTTP_OK) { // 성공
                return parsingNews(conn.getInputStream()); // 뉴스 데이터 파싱
            } else {
                throw new RuntimeException("API 응답 실패: 응답 코드 " + responseCode);
            }
        } catch (IOException e) {
            throw new RuntimeException("데이터를 가져오는 중 오류 발생", e);
        }
    }


    /**
     * 데이터 파싱 메서드
     * @param body InputStream 데이터
     * @return 뉴스 객체 리스트 반환
     */
    private List<NewsDto> parsingNews(InputStream body) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            NewsResponse response = objectMapper.readValue(body, NewsResponse.class);

            // HTML 태그 제거 처리
            response.getItems().forEach(item -> {
                item.setTitle(Jsoup.parse(item.getTitle()).text());
                item.setDescription(Jsoup.parse(item.getDescription()).text());
            });

            return response.getItems();
        } catch (IOException e) {
            throw new RuntimeException("JSON 파싱 중 오류 발생", e);
        }
    }
}
