package com.example.Veco.domain.memberNotification.service;

public interface MemberNotiCommandService {
    void markAsRead(Long memberId, Long alarmId);
}
