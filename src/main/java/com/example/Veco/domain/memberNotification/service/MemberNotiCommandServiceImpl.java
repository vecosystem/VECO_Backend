package com.example.Veco.domain.memberNotification.service;

import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.member.error.MemberErrorStatus;
import com.example.Veco.domain.member.error.MemberHandler;
import com.example.Veco.domain.member.repository.MemberRepository;
import com.example.Veco.domain.memberNotification.entity.MemberNotification;
import com.example.Veco.domain.memberNotification.exception.MemberNotiException;
import com.example.Veco.domain.memberNotification.exception.code.MemberNotiErrorCode;
import com.example.Veco.domain.memberNotification.repository.MemberNotiRepository;
import com.example.Veco.global.auth.user.AuthUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberNotiCommandServiceImpl implements MemberNotiCommandService {

    private final MemberRepository memberRepository;
    private final MemberNotiRepository memberNotiRepository;

    @Override
    @Transactional
    public void markAsRead(AuthUser user, Long alarmId) {
        memberRepository.findBySocialUid(user.getSocialUid()).orElseThrow(() ->
                new MemberHandler(MemberErrorStatus._MEMBER_NOT_FOUND));
        MemberNotification memberNotification = memberNotiRepository
                .findById(alarmId)
                .orElseThrow(() -> new MemberNotiException(MemberNotiErrorCode.NOT_FOUND));
        memberNotification.markAsRead();
    }

    @Override
    @Transactional
    public void deleteMemberNotifications(AuthUser user, List<Long> memberNotiIds) {
        Member member = memberRepository.findBySocialUid(user.getSocialUid()).orElseThrow(() ->
                new MemberHandler(MemberErrorStatus._MEMBER_NOT_FOUND));
        List<MemberNotification> notifications = memberNotiRepository.findAllById(memberNotiIds);

        if (notifications.isEmpty()) {
            throw new MemberNotiException(MemberNotiErrorCode.NOT_FOUND);
        }

        if (notifications.size() != memberNotiIds.size()) {
            throw new MemberNotiException(MemberNotiErrorCode.ID_LIST_INVALID);
        }

        if (notifications.stream().anyMatch(noti -> !noti.getMember().equals(member))) {
            throw new MemberHandler(MemberErrorStatus._FORBIDDEN);
        }

        notifications.forEach(MemberNotification::markAsDeleted);
    }

}