package com.greenheaven.greenheaven_app.controller;

import com.greenheaven.greenheaven_app.dto.PostCommentRequestDto;
import com.greenheaven.greenheaven_app.dto.PostCreateRequestDto;
import com.greenheaven.greenheaven_app.dto.PostListResponseDto;
import com.greenheaven.greenheaven_app.service.CropService;
import com.greenheaven.greenheaven_app.service.NotificationService;
import com.greenheaven.greenheaven_app.service.PostService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PostController {
    private final CropService cropService;
    private final NotificationService notificationService;
    private final PostService postService;

    @GetMapping("/posts")
    public String postsPage(@RequestParam(required = false, defaultValue = "") String search,
                           @RequestParam(defaultValue = "1") int page,
                           Model model) {
        Page<PostListResponseDto> response = postService.postsPage(search, page);
        model.addAttribute("search", search); // 검색어 유지
        model.addAttribute("postListResponseDtos", response);
        model.addAttribute("currentPage", page);  // 현재 페이지 추가
        model.addAttribute("totalPages",  response.getTotalPages());  // 총 페이지 수 추가
        log.info("total pages : {}", response.getTotalPages());
        return "posts";
    }

    @GetMapping("/posts/{postId}")
    public String postPage(@PathVariable String postId, Model model, HttpServletRequest request) {
        String referer = request.getHeader("Referer");

        // 같은 게시글 페이지에서 리다이렉트된 경우에만 조회수가 증가하도록 처리
        boolean shouldIncreaseView = true;
        if (referer != null && referer.contains("/posts/" + postId)) {
            shouldIncreaseView = false;
        }

        model.addAttribute("post", postService.postPage(postId, shouldIncreaseView));
        return "post";
    }

    @GetMapping("/posts/create")
    public String createPostPage() {
        return "createPost";
    }

    @PostMapping("/posts/create")
    public String createPost(@ModelAttribute PostCreateRequestDto request) {
        postService.createPost(request);
        return "redirect:/posts";
    }

    @GetMapping("/posts/{postId}/update")
    public String updatePostPage(@PathVariable String postId, Model model) {
        model.addAttribute("post", postService.postPage(postId, false));
        return "updatePost";
    }

    @PostMapping("/posts/{postId}/update")
    public String updatePost(@ModelAttribute PostCreateRequestDto request,
                                             @PathVariable String postId) {
        postService.updatePost(request, postId);
        return "redirect:/posts/" + postId;
    }

    @PostMapping("/posts/{postId}/delete")
    public String deletePost(@PathVariable String postId) {
        postService.deletePost(postId);
        return "redirect:/posts";
    }

    @PostMapping("/posts/{postId}/comments")
    public String createPostComment(@PathVariable String postId,
                                    @ModelAttribute PostCommentRequestDto request) {
        postService.createPostComment(postId, request);
        return "redirect:/posts/" + postId;
    }

    @PostMapping("/posts/{postId}/comments/{commentId}/delete")
    public String deletePostComment(@PathVariable String postId,
                                                    @PathVariable String commentId) {
        postService.deletePostComment(commentId);
        return "redirect:/posts/" + postId;
    }

}
