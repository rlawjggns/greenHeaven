package com.greenheaven.greenheaven_app.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductionPlan {

    @Id
    @Column(name = "id")
    private UUID id = UUID.randomUUID(); // 생산계획 아이디

    @Column(name = "name", nullable = false)
    private String name; // 생산계획 이름

    @Column(name = "description")
    private String description; // 생산계획 설명

    @Column(name = "start_date", nullable = false)
    private LocalDateTime startDate; // 생산계획 시작일

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate; // 생산계획 종료일

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 유저

    @Builder
    public ProductionPlan(String name, String description, LocalDateTime startDate, LocalDateTime endDate, User user) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
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
        ProductionPlan that = (ProductionPlan) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
