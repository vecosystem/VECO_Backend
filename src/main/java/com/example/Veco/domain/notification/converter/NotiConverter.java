package com.example.Veco.domain.notification.converter;

import com.example.Veco.domain.goal.entity.Goal;
import com.example.Veco.domain.issue.entity.Issue;
import com.example.Veco.domain.memberNotification.entity.MemberNotification;
import com.example.Veco.domain.notification.dto.NotiResDTO;
import com.example.Veco.global.enums.Category;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class NotiConverter {

    // 이슈
    public NotiResDTO.IssuePreViewDTO toIssuePreViewDTO(Issue issue, MemberNotification memberNoti) {
        return NotiResDTO.IssuePreViewDTO.builder()
                .alarmId(memberNoti.getId())
                .name(issue.getName())
                .typeId(issue.getId())
                .title(issue.getTitle())
                .state(issue.getState())
                .priority(issue.getPriority())
                .goalTitle(issue.getGoal().getTitle())
                .isRead(memberNoti.getIsRead())
                .build();
    }

    public List<NotiResDTO.IssuePreViewDTO> toIssuePreviewDTOs(
            List<Issue> issues, List<MemberNotification> memberNotifications
    ) {
        Map<Long, Issue> issueMap = issues.stream()
                .collect(Collectors.toMap(Issue::getId, i -> i));

        return memberNotifications.stream()
                .map(memberNoti -> {
                    Issue issue = issueMap.get(memberNoti.getNotification().getTypeId());
                    return toIssuePreViewDTO(issue, memberNoti);
                })
                .collect(Collectors.toList());
    }

    public NotiResDTO.IssuePreViewListDTO toIssuePreViewListDTO(
            List<NotiResDTO.IssuePreViewDTO> previews, LocalDate deadline
    ) {
        return NotiResDTO.IssuePreViewListDTO.builder()
                .type(Category.ISSUE)
                .deadline(deadline)
                .notificationList(previews)
                .listSize(previews.size())
                .build();
    }

    // 목표

    public List<NotiResDTO.GoalPreViewDTO> toGoalPreviewDTOs(
            List<Goal> goals, List<MemberNotification> memberNotifications
    ) {
        Map<Long, Goal> goalMap = goals.stream()
                .collect(Collectors.toMap(Goal::getId, g -> g));

        return memberNotifications.stream()
                .map(memberNoti -> {
                    Goal goal = goalMap.get(memberNoti.getNotification().getTypeId());
                    return toGoalPreViewDTO(goal, memberNoti);
                })
                .filter(Objects::nonNull)
                .toList();
    }


    public NotiResDTO.GoalPreViewListDTO toGoalPreViewListDTO(
            List<NotiResDTO.GoalPreViewDTO> previews, LocalDate deadline
    ) {
        return NotiResDTO.GoalPreViewListDTO.builder()
                .type(Category.GOAL)
                .deadline(deadline)
                .notificationList(previews)
                .listSize(previews.size())
                .build();
    }


    public NotiResDTO.GoalPreViewDTO toGoalPreViewDTO(Goal goal, MemberNotification memberNoti) {

        return NotiResDTO.GoalPreViewDTO.builder()
                .alarmId(memberNoti.getId())
                .name(goal.getName())
                .typeId(goal.getId())
                .title(goal.getTitle())
                .state(goal.getState())
                .priority(goal.getPriority())
                .isRead(memberNoti.getIsRead())
                .build();
    }
}

