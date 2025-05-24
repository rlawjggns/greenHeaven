package com.greenheaven.greenheaven_app.service;

import com.greenheaven.greenheaven_app.domain.Member;
import com.greenheaven.greenheaven_app.domain.Post;
import com.greenheaven.greenheaven_app.domain.PostComment;
import com.greenheaven.greenheaven_app.dto.*;
import com.greenheaven.greenheaven_app.repository.MemberRepository;
import com.greenheaven.greenheaven_app.repository.PostCommentRepository;
import com.greenheaven.greenheaven_app.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final PostCommentRepository postCommentRepository;
    private final MemberRepository memberRepository;

    /**
     * 게시글 조회
     */
    public Page<PostListResponseDto> postsPage(String search, int page) {
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
    public PostDetailResponseDto postPage(String postId, Boolean shouldIncreaseView) {
        UUID uuid = UUID.fromString(postId);
        Post post = postRepository.findById(uuid)
                .orElseThrow(() -> new NoSuchElementException("해당 게시글을 찾을 수 없습니다."));

        if (shouldIncreaseView) {
            post.increaseViewCount();
            postRepository.save(post); // 영속화 필요
        }

        List<PostCommentResponseDto> postComments = post.getComments().stream()
                .map(PostComment::toDto)
                .toList();

        return PostDetailResponseDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .views(post.getViews())
                .createdDate(post.getCreateDate())
                .updatedDate(post.getUpdateDate())
                .memberName(post.getMember().getName())
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
     * @param request 게시글 댓글 생성에 대한 정보를 담은 DTO
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

        postCommentRepository.save(postComment);
    }

    public void deletePostComment(String commentId) {
        postCommentRepository.deleteById(UUID.fromString(commentId));
    }
}
