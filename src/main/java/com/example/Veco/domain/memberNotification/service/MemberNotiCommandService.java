package com.example.Veco.domain.memberNotification.service;

import com.example.Veco.global.auth.user.AuthUser;

import java.util.List;

public interface MemberNotiCommandService {
    void markAsRead(AuthUser user, Long alarmId);
    void deleteMemberNotifications(AuthUser user, List<Long> alarmIds);
}
