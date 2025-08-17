package com.greenheaven.backend.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class News {

    @Id
    @Column(name = "id")
    private UUID id = UUID.randomUUID(); // 뉴스 아이디

    @Column(name = "title", nullable = false)
    private String title; // 뉴스 제목

    @Column(name = "publisher", nullable = false)
    private String publisher; // 뉴스 발행자 (예: 헤럴드경제)

    @Column(name = "author", nullable = false)
    private String author = ""; // 뉴스 작성자 (작성자가 없는 경우 빈 문자열일 수도 있음)

    @Column(name = "summary", nullable = false)
    private String summary; // 뉴스 요약 (간단한 내용 소개)

    @Column(name = "image_url", nullable = false)
    private String imageUrl; // 뉴스 관련 이미지 URL (큰 이미지)

    @Column(name = "thumbnail_url", nullable = false)
    private String thumbnailUrl; // 뉴스 썸네일 이미지 URL (작은 미리보기 이미지)

    @Column(name = "content_url", nullable = false)
    private String contentUrl; // 뉴스 원본 링크 URL (전체 기사로 이동하는 링크)

    @Enumerated(EnumType.STRING)
    private Sentiment sentiment; // 뉴스의 감정 분석 결과 (긍정, 중립, 부정 등)

    @Column(name = "section", nullable = false)
    private String section; // 뉴스 카테고리 (예: 경제, 정치 등)

    @Column(name = "published_at", nullable = false)
    private String publishedAt; // 뉴스 발행 시간 (예: 2025-03-10T11:29:00 형식)

    public News(String title, String publisher, String author, String summary, String imageUrl, String thumbnailUrl,
                String contentUrl, Sentiment sentiment, String section, String publishedAt) {
        this.title = title;
        this.publisher = publisher;
        this.author = author;
        this.summary = summary;
        this.imageUrl = imageUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.contentUrl = contentUrl;
        this.sentiment = sentiment;
        this.section = section;
        this.publishedAt = publishedAt;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        News news = (News) o;
        return getId() != null && Objects.equals(getId(), news.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
