package com.greenheaven.greenheaven_app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.greenheaven.greenheaven_app.domain.dto.NewsDto;
import com.greenheaven.greenheaven_app.domain.dto.NewsResponse;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class NewsService {
    @Value("${naver.id}")
    private String id;

    @Value("${naver.secret}")
    private String secret;


    public List<NewsDto> getNews() {
        String keyword = null;
        try {
            keyword = URLEncoder.encode("농업","utf-8"); // 농업 키워드 설정
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("검색어 처리 예외가 발생했습니다.");
        }

        String apiURL = "https://openapi.naver.com/v1/search/news.json?query=" + keyword + "&display=10"; // 요청 URL 설정

        Map<String, String> headers = new HashMap<>(); // 헤더 설정 
        headers.put("X-Naver-Client-Id", id); // 아이디 값
        headers.put("X-Naver-Client-Secret", secret); // 시크릿 값
        return get(apiURL, headers);
    }

    private List<NewsDto> get(String apiURL, Map<String, String> headers) {
        HttpURLConnection conn = connect(apiURL); // HttpURLConnection 생성
        try {
            conn.setRequestMethod("GET"); // 요청의 메서드 설정
            for (Map.Entry<String, String> header : headers.entrySet()) { // 요청의 헤더 설정
                conn.setRequestProperty(header.getKey(), header.getValue());
            }

            int responseCode = conn.getResponseCode(); // response 코드 얻기
            if (responseCode == HttpURLConnection.HTTP_OK) { // 성공 시
                return parseNews(conn.getInputStream());
            } else { // 실패 시
                throw new RuntimeException("API 응답을 읽는 데 실패했습니다.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<NewsDto> parseNews(InputStream body) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            // InputStream을 NewsResponse 객체로 변환
            NewsResponse response = objectMapper.readValue(body, NewsResponse.class);

            // 제목이나 설명에 <b> 같은 태그가 섞여있는 문제를 해결
            for (NewsDto item : response.getItems()) {
                item.setTitle(Jsoup.parse(item.getTitle()).text());
                item.setDescription(Jsoup.parse(item.getDescription()).text());
            }

            // dto 리스트 반환
            return response.getItems();
        } catch (Exception e) {
            throw new RuntimeException("JSON 파싱 중 오류 발생");
        } 
    }

    private HttpURLConnection connect(String apiURL) {
        try {
            URL url = new URL(apiURL);
            return (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다.");
        } catch (IOException e) {
            throw new RuntimeException("연결에 실패했습니다.");
        }
    }
}
