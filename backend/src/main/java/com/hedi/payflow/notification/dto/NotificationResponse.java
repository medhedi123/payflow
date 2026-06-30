package com.hedi.payflow.notification.dto;

import com.hedi.payflow.notification.entity.NotificationType;

import java.time.LocalDateTime;

public record NotificationResponse(

        Long id,

        NotificationType type,

        String title,

        String message,

        Boolean read,

        LocalDateTime createdAt

) {
}