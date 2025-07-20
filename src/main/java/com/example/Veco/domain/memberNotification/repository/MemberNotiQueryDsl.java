package com.example.Veco.domain.memberNotification.repository;

import com.example.Veco.domain.memberNotification.entity.MemberNotification;
import com.example.Veco.global.enums.Category;

import java.util.List;

public interface MemberNotiQueryDsl {
    boolean existsByNotificationIdAndMemberId(Long notificationId, Long memberId);
    List<MemberNotification> findByMemberIdAndType(Long memberId, Category alarmType);
}
