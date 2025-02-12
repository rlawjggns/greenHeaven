package com.greenheaven.greenheaven_app.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class PostComment {

    @Id
    @Column(name = "id")
    private UUID uuid = UUID.randomUUID(); // 댓글 아이디

    @Column(name = "content", nullable = false)
    private String content; // 댓글 내용

    @CreatedDate
    @Column(name = "create_date", updatable = false)
    private String createDate; // 댓글 생성일 

    @LastModifiedDate
    @Column(name = "update_date")
    private String updateDate; // 댓글 수정일

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 유저

    @ManyToOne
    @JoinColumn(name = "post_id", nullable = false)
    private Post post; // 게시글


    public PostComment(String content, User user, Post post) {
        this.content = content;
        this.user = user;
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
        return getUuid() != null && Objects.equals(getUuid(), that.getUuid());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
