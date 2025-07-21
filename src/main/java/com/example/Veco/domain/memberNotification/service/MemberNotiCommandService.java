package com.example.Veco.domain.memberNotification.service;

import java.util.List;

public interface MemberNotiCommandService {
    void markAsRead(Long memberId, Long alarmId);
    void deleteMemberNotifications(Long memberId, List<Long> alarmIds);
}
