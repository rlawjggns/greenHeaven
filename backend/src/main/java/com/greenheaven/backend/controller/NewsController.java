package com.greenheaven.backend.controller;

import com.greenheaven.backend.dto.NewsDto;
import com.greenheaven.backend.service.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/news")
@Slf4j
public class NewsController {
    private final NewsService newsService;


    @GetMapping
    public ResponseEntity<List<NewsDto>> getNews() {
        return ResponseEntity.ok(newsService.callNewsAPI());
    }
}
