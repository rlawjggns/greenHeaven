package com.greenheaven.greenheaven_app.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NewsDto {
    private String title;
    private String originallink;
    private String link;
    private String description;
    private String pubDate;
}
