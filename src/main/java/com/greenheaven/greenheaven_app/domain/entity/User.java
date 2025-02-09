package com.greenheaven.greenheaven_app.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
public class User {

    @Id
    @Column(name = "id")
    private UUID id = UUID.randomUUID(); // 사용자 아이디

    @Column(name = "name", nullable = false)
    private String name; // 사용자 이름

    @Column(name = "password", nullable = false)
    private String password; // 사용자 비밀번호

    @Column(name = "email", nullable = false)
    private String email; // 사용자 이메일

    @CreatedDate
    @Column(name = "create_date", nullable = false)
    private LocalDate createDate; // 사용자 생성일

    @LastModifiedDate
    @Column(name = "update_date")
    private LocalDate updateDate; // 사용자 수정일

    @Enumerated
    @Column(name = "role")
    private UserRole role; // 사용자 권한
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Crop> crops = new ArrayList<>(); // 작물들

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Alert> alerts = new ArrayList<>(); // 경고들

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Soil> soils = new ArrayList<>(); // 토양분석들

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_id", nullable = false)
    private Subscription subscription; // 구독

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Payment> payments = new ArrayList<>(); // 결제들

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ProductionPlan> productionPlans = new ArrayList<>(); // 생산계획들

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Task> tasks = new ArrayList<>(); // 작업들

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Notification> notifications = new ArrayList<>(); // 알림들

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Location> locations = new ArrayList<>(); // 위치들


    public User(String name, String password, String email, Subscription subscription) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.subscription = subscription;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        User user = (User) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
