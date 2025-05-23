package com.greenheaven.greenheaven_app.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
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

    @Column(name = "harvest_date", nullable = false)
    private LocalDate harvestDate; // 작물 수확일

    @Column(name = "quantity", nullable = false)
    private Double quantity; // 작물 수량

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Builder
    public Crop(String name, CropType type, LocalDate plantDate, LocalDate harvestDate, Member member, Double quantity) {
        this.name = name;
        this.type = type;
        this.plantDate = plantDate;
        this.harvestDate = harvestDate;
        this.member = member;
        this.quantity = quantity;
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

    public void updateHarvestDate(Integer adjustDays) {
        this.harvestDate = this.harvestDate.plusDays(adjustDays);
    }
}
