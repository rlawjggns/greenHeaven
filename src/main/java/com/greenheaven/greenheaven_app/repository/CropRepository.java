package com.greenheaven.greenheaven_app.repository;

import com.greenheaven.greenheaven_app.domain.Crop;
import com.greenheaven.greenheaven_app.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CropRepository extends JpaRepository<Crop, UUID> {
    List<Crop> findTop10ByMember(Member member);

    List<Crop> findByMember(Member member);
}
