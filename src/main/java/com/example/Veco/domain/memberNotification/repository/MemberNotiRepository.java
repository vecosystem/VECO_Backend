package com.example.Veco.domain.memberNotification.repository;

import com.example.Veco.domain.memberNotification.entity.MemberNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberNotiRepository extends JpaRepository<MemberNotification, Long>, MemberNotiQueryDsl {
    Optional<MemberNotification> findByNotificationIdAndMemberId(Long notificationId, Long memberId);
}
