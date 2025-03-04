package com.greenheaven.greenheaven_app.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
public class Weather {

    @Id
    @Column(name = "id")
    private UUID uuid = UUID.randomUUID(); // 기상정보 아이디

    @Column(name = "temperature", nullable = false)
    private Double temperature; // 기상정보 온도

    @Column(name = "humidity", nullable = false)
    private Double humidity; // 기상정보 습도

    @Column(name = "condition", nullable = false)
    private String condition; // 기상정보 기상상태

    @Column(name = "date", nullable = false)
    private LocalDate date; // 기상정보 날짜

    @Column(name = "location", nullable = false)
    private String location; // 기상정보 위치

    @Builder
    public Weather(Double temperature, Double humidity, String condition, LocalDate date, String location) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.condition = condition;
        this.date = date;
        this.location = location;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Weather weather = (Weather) o;
        return getUuid() != null && Objects.equals(getUuid(), weather.getUuid());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
