package com.greenheaven.greenheaven_app.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class Task {

    @Id
    @Column(name = "id")
    private UUID uuid = UUID.randomUUID(); // 작업 아이디

    @Column(name = "name", nullable = false)
    private String name; // 작업 이름

    @Column(name = "description")
    private String description; // 작업 설명

    @Column(name = "end_date", nullable = false)
    private LocalDateTime endDate; // 작업 마감일

    @Enumerated
    @Column(name = "status", nullable = false)
    private TaskStatus status = TaskStatus.PENDING; // 작업 상태

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "production_plan_id", nullable = false)
    private ProductionPlan productionPlan; // 생산계획

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // 유저


    public Task(String name, LocalDateTime endDate, ProductionPlan productionPlan, User user) {
        this.name = name;
        this.endDate = endDate;
        this.productionPlan = productionPlan;
        this.user = user;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Task task = (Task) o;
        return getUuid() != null && Objects.equals(getUuid(), task.getUuid());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
