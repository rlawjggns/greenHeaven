package com.greenheaven.backend.repository;

import com.greenheaven.backend.domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    List<Notification> findTop10ByReceiverEmailOrderByCreatedDateDesc(String receiverEmail);

    List<Notification> findByReceiverEmail(String receiverEmail);
}
