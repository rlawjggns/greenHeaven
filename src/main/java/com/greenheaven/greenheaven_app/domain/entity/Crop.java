package com.greenheaven.greenheaven_app.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Crop {

    @Id
    @Column(name = "id")
    private UUID id = UUID.randomUUID(); // 작물 아아디

    @Column(name = "name", nullable = false)
    private String name; // 작물 이름

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private CropType type; // 작물 종류

    @Column(name = "plant_date", nullable = false)
    private LocalDate plantDate; // 작물 파종일

    @Column(name = "harvest_date")
    private LocalDate harvestDate; // 작물 수확일

    @OneToMany(mappedBy = "crop", cascade = CascadeType.ALL)
    private List<Soil> soils = new ArrayList<>(); // 토양분석들

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public Crop(String name, CropType type, LocalDate plantDate, LocalDate harvestDate, User user) {
        this.name = name;
        this.type = type;
        this.plantDate = plantDate;
        this.harvestDate = harvestDate;
        this.user = user;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Crop crop = (Crop) o;
        return getId() != null && Objects.equals(getId(), crop.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
