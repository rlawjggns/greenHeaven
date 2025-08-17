package com.greenheaven.backend.controller;

import com.greenheaven.backend.service.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
@Slf4j
public class NewsController {
    private final NewsService newsService;


    @GetMapping("/news")
    public String getNews(Model model) {
        model.addAttribute("newsList", newsService.callNewsAPI());
        return "news";
    }
}
