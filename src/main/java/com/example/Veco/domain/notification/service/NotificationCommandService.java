package com.example.Veco.domain.notification.service;

import com.example.Veco.global.event.NotificationSourceCreatedEvent;

public interface NotificationCommandService {
    void createNotificationAndMemberNotifications(NotificationSourceCreatedEvent event);
}
