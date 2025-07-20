package com.example.Veco.domain.notification.service;

import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.memberNotification.entity.MemberNotification;
import com.example.Veco.domain.memberNotification.repository.MemberNotiRepository;
import com.example.Veco.domain.notification.entity.Notification;
import com.example.Veco.domain.notification.repository.NotiRepository;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.global.enums.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class NoticommandServiceImpl implements NotiCommandService{

    private final NotiRepository notificationRepository;
    private final MemberNotiRepository memberNotiRepo;

    // 알림 동적 생성
    // FIXME : 사용자가 삭제한 membernotification 재생성 X
    @Transactional
    public void createNotification(Category type, Long typeId, Team team, Member member) {

        // Notification 중복 확인
        Notification notification = notificationRepository
                .findByTargetInfo(type, typeId, team)
                .orElseGet(() -> {
                    // 없으면 생성
                    Notification newNotification = Notification.builder()
                            .type(type)
                            .typeId(typeId)
                            .expireAt(LocalDate.now().plusDays(1))
                            .team(team)
                            .build();
                    return notificationRepository.save(newNotification);
                });

        // MemberNotification 중복 확인
        boolean alreadyExists = memberNotiRepo
                .existsByNotificationIdAndMemberId(notification.getId(), member.getId());

        if (!alreadyExists) {
            MemberNotification memberNotification = MemberNotification.builder()
                    .notification(notification)
                    .member(member)
                    .build();
            memberNotiRepo.save(memberNotification);
        }
    }

}
