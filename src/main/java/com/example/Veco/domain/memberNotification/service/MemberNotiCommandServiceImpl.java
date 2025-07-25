package com.example.Veco.domain.memberNotification.service;

import com.example.Veco.domain.memberNotification.entity.MemberNotification;
import com.example.Veco.domain.memberNotification.repository.MemberNotiRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberNotiCommandServiceImpl implements MemberNotiCommandService {

    private final MemberNotiRepository memberNotiRepository;

    @Override
    @Transactional
    public void markAsRead(Long memberId, Long alarmId) {
        MemberNotification memberNotification = memberNotiRepository
                .findById(alarmId)
                .orElseThrow(() -> new IllegalArgumentException("해당 알림이 존재하지 않습니다.")); // FIXME
        // TODO : 사용자 검증
        memberNotification.markAsRead();
    }

    @Override
    @Transactional
    public void deleteMemberNotifications(Long memberId, List<Long> memberNotiIds) {
        List<MemberNotification> notifications = memberNotiRepository.findAllById(memberNotiIds);
        notifications.stream()
                .filter(noti -> noti.getMember().getId().equals(memberId))
                .forEach(MemberNotification::markAsDeleted);
    }

}
