package com.example.Veco.domain.notification.service;

import com.example.Veco.domain.memberNotification.repository.MemberNotiRepository;
import com.example.Veco.domain.notification.entity.Notification;
import com.example.Veco.domain.notification.repository.NotiRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationCleanUpScheduler {

    private final NotiRepository notificationRepository;
    private final MemberNotiRepository memberNotiRepo;

    @Scheduled(cron = "0 0 0 * * *")  // hard delete
    @Transactional
    public void deleteExpiredNotifications() {
        LocalDate today = LocalDate.now();
        List<Notification> expiredNotifications = notificationRepository.findByExpireAt(today);

        for (Notification notification : expiredNotifications) {
            memberNotiRepo.deleteByNotificationId(notification.getId());
            notificationRepository.delete(notification);
        }
        log.info("[알림 정리] {}개의 만료된 알림을 삭제했습니다.", expiredNotifications.size());
    }

}
