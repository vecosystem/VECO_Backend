package com.example.Veco.domain.notification.service;

import com.example.Veco.domain.goal.entity.Goal;
import com.example.Veco.domain.goal.repository.GoalRepository;
import com.example.Veco.domain.issue.entity.Issue;
import com.example.Veco.domain.issue.repository.IssueRepository;
import com.example.Veco.domain.member.repository.MemberRepository;
import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.memberNotification.entity.MemberNotification;
import com.example.Veco.domain.memberNotification.repository.MemberNotiRepository;
import com.example.Veco.domain.notification.converter.NotiConverter;
import com.example.Veco.domain.notification.dto.NotiResDTO;
import com.example.Veco.domain.reminder.service.ReminderService;
import com.example.Veco.global.enums.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotiQueryServiceImpl implements NotiQueryService {

    private final MemberRepository memberRepository;
    private final MemberNotiRepository memberNotiRepository;
    private final IssueRepository issueRepository;
    private final GoalRepository goalRepository;
    private final NotiConverter notiConverter;
    private final ReminderService reminderService;

    public Object getNotiList(Long memberId, Category alarmType, String filter) {

        // FIXME : 예외처리수정
        Optional<Member> member = memberRepository.findById(memberId);
        reminderService.handleReminder(member.orElse(null)); // 알림 동적 생성

        LocalDate deadline = LocalDate.now();

        List<MemberNotification> memberNotis = memberNotiRepository.findByMemberIdAndTypeAndNotDeleted(memberId, alarmType);

        List<Long> typeIds = memberNotis.stream()
                .map(memberNoti -> memberNoti.getNotification().getTypeId())
                .collect(Collectors.toList());

        if (alarmType == Category.ISSUE) {

            List<Issue> issues = issueRepository.findByIdIn(typeIds);
            List<NotiResDTO.IssuePreViewDTO> previews = notiConverter.toIssuePreviewDTOs(issues, memberNotis);

            if ("priority".equalsIgnoreCase(filter)) {
                return notiConverter.toIssuePreviewListByPriority(previews, deadline);
            } else { // default = state
                return notiConverter.toIssuePreviewListByState(previews, deadline);
            }

        } else if (alarmType == Category.GOAL) {

            List<Goal> goals = goalRepository.findByIdIn(typeIds);
            List<NotiResDTO.GoalPreViewDTO> previews = notiConverter.toGoalPreviewDTOs(goals, memberNotis);

            if ("priority".equalsIgnoreCase(filter)) {
                return notiConverter.toGoalPreviewListByPriority(previews, deadline);
            } else {
                return notiConverter.toGoalPreviewListByState(previews, deadline);
            }

        } else {
            // TODO : External 추가
            throw new IllegalArgumentException("지원하지 않는 알림 타입입니다.");
        }

    }
}