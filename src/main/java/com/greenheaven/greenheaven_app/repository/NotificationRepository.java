package com.greenheaven.greenheaven_app.repository;

import com.greenheaven.greenheaven_app.domain.entity.Notification;
import com.greenheaven.greenheaven_app.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findTop10ByUser(User user);
}
