package com.example.Veco.domain.notification.service;

import com.example.Veco.domain.external.entity.External;
import com.example.Veco.domain.external.repository.ExternalRepository;
import com.example.Veco.domain.goal.entity.Goal;
import com.example.Veco.domain.goal.repository.GoalRepository;
import com.example.Veco.domain.issue.entity.Issue;
import com.example.Veco.domain.issue.repository.IssueRepository;
import com.example.Veco.domain.member.error.MemberErrorStatus;
import com.example.Veco.domain.member.error.MemberHandler;
import com.example.Veco.domain.member.repository.MemberRepository;
import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.memberNotification.entity.MemberNotification;
import com.example.Veco.domain.memberNotification.repository.MemberNotiRepository;
import com.example.Veco.domain.notification.converter.NotiConverter;
import com.example.Veco.domain.notification.dto.NotiResDTO.*;
import com.example.Veco.domain.notification.enums.FilterType;
import com.example.Veco.domain.notification.exception.NotificationException;
import com.example.Veco.domain.notification.exception.code.NotiErrorCode;
import com.example.Veco.domain.reminder.service.ReminderService;
import com.example.Veco.global.auth.user.AuthUser;
import com.example.Veco.global.enums.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotiQueryServiceImpl implements NotiQueryService {

    private final MemberRepository memberRepository;
    private final MemberNotiRepository memberNotiRepository;
    private final IssueRepository issueRepository;
    private final GoalRepository goalRepository;
    private final ExternalRepository externalRepository;
    private final NotiConverter notiConverter;
    private final ReminderService reminderService;

    public GroupedNotiList getNotiList(AuthUser user, Category alarmType, String filter) {
        Member member = memberRepository.findBySocialUid(user.getSocialUid()).orElseThrow(() ->
                new MemberHandler(MemberErrorStatus._MEMBER_NOT_FOUND));
        reminderService.handleReminder(member); // 알림 동적 생성

        LocalDate deadline = LocalDate.now();
        List<MemberNotification> memberNotis = memberNotiRepository.findByMemberAndTypeAndNotDeleted(member, alarmType);
        List<Long> typeIds = memberNotis.stream()
                .map(mn -> mn.getNotification().getTypeId())
                .collect(Collectors.toList());

        FilterType filterType = FilterType.from(filter);

        return switch (alarmType) {
            case ISSUE -> handleIssue(typeIds, memberNotis, filterType, deadline);
            case GOAL -> handleGoal(typeIds, memberNotis, filterType, deadline);
            case EXTERNAL -> handleExternal(typeIds, memberNotis, filterType, deadline);
            default -> throw new NotificationException(NotiErrorCode.QUERY_INVALID);
        };
    }
    private GroupedNotiList handleIssue(List<Long> typeIds, List<MemberNotification> memberNotis,
                                        FilterType filter, LocalDate deadline) {
        List<Issue> issues = issueRepository.findByIdIn(typeIds);
        Map<Long, MemberNotification> memberNotiMap = memberNotis.stream()
                .collect(Collectors.toMap(
                        mn -> mn.getNotification().getTypeId(),
                        Function.identity()
                ));
        List<IssuePreViewDTO> previews = notiConverter.toIssuePreviewDTOs(issues, memberNotiMap);

        return switch (filter) {
            case PRIORITY -> notiConverter.toIssuePreviewListByPriority(previews, deadline);
            case GOAL -> notiConverter.toIssuePreviewListByGoal(previews, deadline);
            default -> notiConverter.toIssuePreviewListByState(previews, deadline);
        };
    }

    private GroupedNotiList handleGoal(List<Long> typeIds, List<MemberNotification> memberNotis,
                                       FilterType filter, LocalDate deadline) {
        List<Goal> goals = goalRepository.findByIdIn(typeIds);
        Map<Long, MemberNotification> memberNotiMap = memberNotis.stream()
                .collect(Collectors.toMap(
                        mn -> mn.getNotification().getTypeId(),
                        Function.identity()
                ));
        List<GoalPreViewDTO> previews = notiConverter.toGoalPreviewDTOs(goals, memberNotiMap);

        return filter == FilterType.PRIORITY
                ? notiConverter.toGoalPreviewListByPriority(previews, deadline)
                : notiConverter.toGoalPreviewListByState(previews, deadline);
    }

    private GroupedNotiList handleExternal(List<Long> typeIds, List<MemberNotification> memberNotis,
                                           FilterType filter, LocalDate deadline) {
        List<External> externals = externalRepository.findByIdIn(typeIds);
        Map<Long, MemberNotification> memberNotiMap = memberNotis.stream()
                .collect(Collectors.toMap(
                        mn -> mn.getNotification().getTypeId(),
                        Function.identity()
                ));
        List<ExternalPreViewDTO> previews = notiConverter.toExternalPreviewDTOs(externals, memberNotiMap);

        return switch (filter) {
            case PRIORITY -> notiConverter.toExternalPreviewListByPriority(previews, deadline);
            case GOAL -> notiConverter.toExternalPreviewListByGoal(previews, deadline);
            case EXTERNAL -> notiConverter.toExternalPreviewListByExternal(previews, deadline);
            default -> notiConverter.toExternalPreviewListByState(previews, deadline);
        };
    }

}