package com.example.Veco.domain.memberNotification.service;

import com.example.Veco.domain.memberNotification.entity.MemberNotification;
import com.example.Veco.domain.memberNotification.repository.MemberNotiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberNotiCommandServiceImpl implements MemberNotiCommandService {

    private final MemberNotiRepository memberNotiRepository;

    @Override
    @Transactional
    public void markAsRead(Long memberId, Long alarmId) {
        MemberNotification memberNotification = memberNotiRepository
                .findByNotificationIdAndMemberId(alarmId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 알림이 존재하지 않습니다.")); // FIXME
        memberNotification.markAsRead();
    }
}
