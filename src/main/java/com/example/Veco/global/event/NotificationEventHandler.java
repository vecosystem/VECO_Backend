package com.example.Veco.global.event;

import com.example.Veco.domain.notification.service.NotificationCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationEventHandler {
    private final NotificationCommandService notificationCommandService;

    @TransactionalEventListener
    public void handleNotificationSourceCreated(NotificationSourceCreatedEvent event) {
        notificationCommandService.createNotificationAndMemberNotifications(event);
    }

}
