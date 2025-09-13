package com.greenheaven.backend.service;

import com.greenheaven.backend.domain.*;
import com.greenheaven.backend.dto.*;
import com.greenheaven.backend.repository.MemberRepository;
import com.greenheaven.backend.repository.NotificationRepository;
import com.greenheaven.backend.repository.PostCommentRepository;
import com.greenheaven.backend.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final NotificationRepository notificationRepository;
    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;
    private final MemberRepository memberRepository;
    private final NotificationService notificationService;

    /**
     * 게시글 조회
     */
    public Page<PostListResponseDto> getPosts(String search, int page) {
        Pageable pageable = PageRequest.of(page-1, 2,  Sort.by(Sort.Direction.DESC, "createDate"));
        Page<Post> posts;

        if (search.isEmpty()) posts = postRepository.findAll(pageable);
        else posts = postRepository.findByTitleContaining(search, pageable);

        List<PostListResponseDto> dtos = posts.stream()
                .map(Post::toDto)
                .toList();
        return new PageImpl<>(dtos, pageable, posts.getTotalElements()); // Page 유지됨!
    }

    /**
     * 게시글 저장
     * @param request 게시글 저장에 필요한 정보를 담은 DTO
     */
    public void createPost(PostCreateRequestDto request) {
        // 로그인한 사용자의 이메일 조회 및 사용자 객체 조회
        String memberEmail = MemberService.getAuthenticatedMemberEmail();
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다."));

        // 게시글 생성 및 저장
        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .member(member)
                .build();

        postRepository.save(post);
    }

    /**
     * 게시글 상세 조회
     * @param postId 게시글 고유 키
     */
    public PostDetailResponseDto getPost(String postId) {
        UUID uuid = UUID.fromString(postId);
        Post post = postRepository.findById(uuid)
                .orElseThrow(() -> new NoSuchElementException("해당 게시글을 찾을 수 없습니다."));

        post.increaseViewCount();
        postRepository.save(post);

        List<PostCommentResponseDto> postComments = post.getComments().stream()
                .map(PostComment::toDto)
                .sorted(Comparator.comparing(PostCommentResponseDto::getCreatedDate).reversed())
                .toList();

        return PostDetailResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .views(post.getViews())
                .createdDate(post.getCreateDate())
                .updatedDate(post.getUpdateDate())
                .memberName(post.getMember().getName())
                .memberEmail(post.getMember().getEmail())
                .postComments(postComments)
                .build();
    }

    /**
     * 게시글 삭제
     * @param postId 삭제할 게시글의 고유키
     */
    public void deletePost(String postId) {
        UUID uuid = UUID.fromString(postId);
        postRepository.deleteById(uuid);
    }

    /**
     * 게시글 수정
     * @param request 게시글 수정 정보를 담은 DTO
     * @param postId 수정할 게시글의 고유키
     */
    public void updatePost(PostCreateRequestDto request, String postId) {
        UUID uuid = UUID.fromString(postId);
        Post post = postRepository.findById(uuid)
                .orElseThrow(() -> new NoSuchElementException("해당 게시글을 찾을 수 없습니다."));

        post.edit(request);
    }

    /**
     * 게시글 댓글 생성
     * @param postId 소속 게시글의 고유 키
     */
    public void createPostComment(String postId, PostCommentRequestDto request) {
        // 로그인한 사용자의 이메일 조회 및 사용자 객체 조회
        String memberEmail = MemberService.getAuthenticatedMemberEmail();
        Member member = memberRepository.findByEmail(memberEmail)
                .orElseThrow(() -> new NoSuchElementException("해당 유저를 찾을 수 없습니다."));

        UUID uuid = UUID.fromString(postId);
        Post post = postRepository.findById(uuid)
                .orElseThrow(() -> new NoSuchElementException("해당 게시글을 찾을 수 없습니다."));


        PostComment postComment = PostComment.builder()
                .content(request.getContent())
                .member(member)
                .post(post)
                .build();

        // 이 댓글이 달린 게시글의 작성자 조회
        Member postMember = post.getMember();

        // 알림 객체 만들기
        Notification notification = Notification.builder()
                .type(NotificationType.GENERAL)
                .receiverEmail(postMember.getEmail())
                .content("내 게시글에 새로운 댓글이 달렸습니다.")
                .build();

        // 알림 저장 및 보내기
        notificationRepository.save(notification);
        notificationService.send(notification);

        postCommentRepository.save(postComment);
    }

    public void deletePostComment(String commentId) {
        postCommentRepository.deleteById(UUID.fromString(commentId));
    }
}
