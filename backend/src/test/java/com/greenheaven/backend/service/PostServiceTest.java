package com.greenheaven.backend.service;


import com.greenheaven.backend.domain.Post;
import com.greenheaven.backend.dto.PostListResponseDto;
import com.greenheaven.backend.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {
    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @DisplayName("빈 검색어로 게시글 조회 시 전체 게시글 목록을 반환한다.")
    @Test
    void getPosts_when_search_is_empty() {
        // given
        // 가짜 데이터베이스 역할을 하는 Mock 객체 설정
        Page<Post> emptyPage = new PageImpl<>(Collections.emptyList());
        when(postRepository.findAll(any(PageRequest.class))).thenReturn(emptyPage);

        // when
        // 빈 검색어와 1페이지로 조회
        Page<PostListResponseDto> result = postService.getPosts("", 1);

        // then
        // 예상하는 결과는 빈 페이지 객체
        assertThat(result).isNotNull();
        assertThat(result.getTotalElements()).isEqualTo(0);
        assertThat(result.getContent()).isEmpty();
    }
}
