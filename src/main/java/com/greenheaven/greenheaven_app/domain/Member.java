package com.greenheaven.greenheaven_app.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.util.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Member {

    @Id
    @Column(name = "id")
    private UUID id = UUID.randomUUID(); // 사용자 아이디

    @Column(name = "name", nullable = false)
    private String name; // 사용자 이름

    @Column(name = "password", nullable = false)
    private String password; // 사용자 비밀번호

    @Column(name = "email", nullable = false)
    private String email; // 사용자 이메일


    @Column(name = "address", nullable = false)
    private String address; // 도로명 주소

    @CreatedDate
    @Column(name = "create_date", updatable = false)
    private LocalDate createDate; // 사용자 생성일

    @LastModifiedDate
    @Column(name = "update_date")
    private LocalDate updateDate; // 사용자 수정일

    @Column(name = "longitude")
    private Float longitude; // 경도 x

    @Column(name = "latitude")
    private Float latitude; // 위도 y


    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private UserRole role = UserRole.USER; // 사용자 권한
    
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<Crop> crops = new ArrayList<>(); // 작물들

    @Builder
    public Member(String name, String password, String email, String address, Float latitude, Float longitude, UserRole role) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.role = role;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Member member = (Member) o;
        return getId() != null && Objects.equals(getId(), member.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateAddress(String address, Map<String, Float> coordinates) {
        this.address = address;
        this.latitude = coordinates.get("latitude");
        this.longitude = coordinates.get("longitude");
    }
}
