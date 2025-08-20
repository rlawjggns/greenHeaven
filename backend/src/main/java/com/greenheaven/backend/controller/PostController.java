package com.greenheaven.backend.controller;

import com.greenheaven.backend.dto.*;
import com.greenheaven.backend.service.CropService;
import com.greenheaven.backend.service.NotificationService;
import com.greenheaven.backend.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/posts")
@Slf4j
public class PostController {
    private final CropService cropService;
    private final NotificationService notificationService;
    private final PostService postService;

    @GetMapping
    public ResponseEntity<Page<PostListResponseDto>> getPosts(
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(defaultValue = "1") int page) {
        return ResponseEntity.ok(postService.getPosts(search, page));
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDetailResponseDto> getPost(@PathVariable("postId") String id) {
        return ResponseEntity.ok(postService.getPost(id));
    }

    @PostMapping
    public void createPost(@RequestBody PostCreateRequestDto request) {
        log.info("createPost: {} ,{}", request.getTitle(), request.getContent());
        postService.createPost(request);
    }

    @PatchMapping("/{postId}")
    public void updatePost(@RequestBody PostCreateRequestDto request, @PathVariable("postId") String id) {
        postService.updatePost(request, id);
    }

    @DeleteMapping("/{postId}")
    public void deletePost(@PathVariable("postId") String id) {
        postService.deletePost(id);
    }

    @PostMapping("/{postId}/comments")
    public void createPostComment(@PathVariable("postId") String id, @RequestBody PostCommentRequestDto request) {
        postService.createPostComment(id, request);
    }

    @DeleteMapping("/{postId}/comments/{commentId}")
    public void deletePostComment(@PathVariable("postId") String postId, @PathVariable String commentId) {
        postService.deletePostComment(commentId);
    }

}
