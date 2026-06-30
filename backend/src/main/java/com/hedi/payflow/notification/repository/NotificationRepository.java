package com.hedi.payflow.notification.repository;

import com.hedi.payflow.notification.entity.Notification;
import com.hedi.payflow.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserOrderByCreatedAtDesc(User user);
    List<Notification> findByUserAndReadFalse(User user);

    long countByUserAndReadFalse(User user);
}