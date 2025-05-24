package com.greenheaven.greenheaven_app.domain;

import com.greenheaven.greenheaven_app.dto.PostCommentResponseDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class PostComment {

    @Id
    @Column(name = "id")
    private UUID id = UUID.randomUUID(); // 댓글 아이디

    @Column(name = "content", nullable = false)
    private String content; // 댓글 내용

    @CreatedDate
    @Column(name = "create_date", updatable = false)
    private LocalDateTime createDate; // 댓글 생성일

    @LastModifiedDate
    @Column(name = "update_date")
    private LocalDateTime updateDate; // 댓글 수정일

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member; // 유저

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post; // 게시글

    @Builder
    public PostComment(String content, Member member, Post post) {
        this.content = content;
        this.member = member;
        this.post = post;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        PostComment that = (PostComment) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    public PostCommentResponseDto toDto() {
        return PostCommentResponseDto.builder()
                .content(this.getContent())
                .createdDate(this.getCreateDate())
                .memberName(this.getMember().getName())
                .id(this.getId())
                .build();
    }
}
