package com.example.Veco.domain.reminder.service;

import com.example.Veco.global.enums.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class ReminderScheduler {

    // TODO:  마감일이 변경된 이슈/목표/외부 캐시

    private final ReminderCacheService reminderCacheService;
    private final ReminderTargetService reminderTargetService;


    // ⏳ 자정마다 캐시 초기화
    @Scheduled(cron = "0 0 0 * * *")
    public void runReminderJobInOrder() {
        clearOldCache();
        cacheTodayReminders();
    }

    // ⏰ 오늘 마감 대상 캐시
    public void cacheTodayReminders() {
        LocalDate today = LocalDate.now();

        reminderCacheService.cacheTodayTypeIds(Category.ISSUE,
                reminderTargetService.findDueIssueIds(today));

        reminderCacheService.cacheTodayTypeIds(Category.GOAL,
                reminderTargetService.findDueGoalIds(today));

        reminderCacheService.cacheTodayTypeIds(Category.EXTERNAL,
                reminderTargetService.findDueExternalIds(today));
    }

    // 🗑 어제 알림 삭제
    public void clearOldCache() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        for (Category type : Category.values()) {
            reminderCacheService.clearTypeIds(type, yesterday);
        }
    }

}


