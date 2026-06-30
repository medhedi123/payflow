package com.hedi.payflow.notification.service;

import com.hedi.payflow.notification.dto.NotificationResponse;
import com.hedi.payflow.notification.entity.Notification;
import com.hedi.payflow.notification.entity.NotificationType;
import com.hedi.payflow.notification.repository.NotificationRepository;
import com.hedi.payflow.user.entity.User;
import com.hedi.payflow.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public void create(
            User user,
            NotificationType type,
            String title,
            String message
    ) {

        Notification notification = Notification.builder()
                .user(user)
                .type(type)
                .title(title)
                .message(message)
                .read(false)
                .build();

        notificationRepository.save(notification);
    }

    public List<NotificationResponse> myNotifications(Authentication authentication) {

        User user = getCurrentUser(authentication);

        return notificationRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public long unreadCount(Authentication authentication) {

        User user = getCurrentUser(authentication);

        return notificationRepository.countByUserAndReadFalse(user);
    }

    public void markAsRead(Long notificationId, Authentication authentication) {

        User user = getCurrentUser(authentication);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        notification.setRead(true);

        notificationRepository.save(notification);
    }

    public void markAllAsRead(Authentication authentication) {

        User user = getCurrentUser(authentication);

        List<Notification> notifications =
                notificationRepository.findByUserAndReadFalse(user);

        notifications.forEach(notification -> notification.setRead(true));

        notificationRepository.saveAll(notifications);
    }

    public void delete(Long notificationId, Authentication authentication) {

        User user = getCurrentUser(authentication);

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Access denied");
        }

        notificationRepository.delete(notification);
    }

    private User getCurrentUser(Authentication authentication) {
        return userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getType(),
                notification.getTitle(),
                notification.getMessage(),
                notification.getRead(),
                notification.getCreatedAt()
        );
    }
}