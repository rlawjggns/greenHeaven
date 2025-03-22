package com.greenheaven.greenheaven_app.repository;

import com.greenheaven.greenheaven_app.domain.entity.Weather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WeatherRepository extends JpaRepository<Weather, UUID> {
}
