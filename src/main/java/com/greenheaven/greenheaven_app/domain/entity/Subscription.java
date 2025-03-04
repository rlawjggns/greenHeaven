package com.greenheaven.greenheaven_app.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Subscription {

    @Id
    @Column(name = "id")
    private UUID id = UUID.randomUUID(); // 구독 아이디

    @Enumerated(EnumType.STRING)
    @Column(name = "plan", nullable = false)
    private SubPlan plan = SubPlan.FREE; // 구독 플랜

    @CreatedDate
    @Column(name = "start_date", updatable = false)
    private LocalDateTime startDate; // 구독 시작일

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate; // 구독 종료일

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 유저

    @Builder
    public Subscription(LocalDateTime endDate, User user) {
        this.endDate = endDate;
        this.user = user;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Subscription that = (Subscription) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
