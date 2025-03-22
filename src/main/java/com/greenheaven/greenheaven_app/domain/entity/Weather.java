package com.greenheaven.greenheaven_app.domain.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Weather {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id; // 기상정보 아이디

    @Column(name = "date", nullable = false)
    private LocalDate date; // 기상정보 날짜

    @Column(name = "time", nullable = false)
    private LocalTime time; // 기상정보 시간

    @Enumerated(EnumType.STRING)
    @Column(name = "sky", nullable = false)
    private Sky sky; // 기상정보 하늘상태

    @Column(name = "min_temp", nullable = false)
    private Double minTemp; // 기상정보 일 최저기온

    @Column(name = "max_temp", nullable = false)
    private Double maxTemp; // 기상정보 일 최고기온
    
    @Column(name = "temperature", nullable = false)
    private Integer temperature; // 기상정보 시간 별 기온

    @Column(name = "humidity", nullable = false)
    private Integer humidity; // 기상정보 시간 별 습도(%)

    @Column(name = "probability", nullable = false)
    private Integer probability; // 기상정보 시간 별 강수확률(%)

    @Enumerated(EnumType.STRING)
    @Column(name = "precipitation", nullable = false)
    private Precipitation precipitation; // 기상정보 시간 별 강수형태

    @Column(name = "wind_direction", nullable = false)
    private Integer windDirection; // 기상정보 시간 별 풍향

    @Column(name = "winding", nullable = false)
    private Double winding; // 기상정보 시간 별 풍속

    @Column(name = "location_x", nullable = false)
    private Integer locationX; // 기상정보 x 좌표값

    @Column(name = "location_y", nullable = false)
    private Integer locationY; // 기상정보 y 좌표값

    @Builder
    public Weather(LocalDate date, LocalTime time, Sky sky, Double minTemp, Double maxTemp, Integer temperature, Integer humidity, Integer probability, Precipitation precipitation, Integer windDirection, Double winding, Integer locationX, Integer locationY) {
        this.date = date;
        this.time = time;
        this.sky = sky;
        this.minTemp = minTemp;
        this.maxTemp = maxTemp;
        this.temperature = temperature;
        this.humidity = humidity;
        this.probability = probability;
        this.precipitation = precipitation;
        this.windDirection = windDirection;
        this.winding = winding;
        this.locationX = locationX;
        this.locationY = locationY;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Weather weather = (Weather) o;
        return getId() != null && Objects.equals(getId(), weather.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
