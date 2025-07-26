package com.example.Veco.domain.notification.converter;

import com.example.Veco.domain.goal.entity.Goal;
import com.example.Veco.domain.issue.entity.Issue;
import com.example.Veco.domain.memberNotification.entity.MemberNotification;
import com.example.Veco.domain.notification.dto.NotiResDTO;
import com.example.Veco.global.enums.Category;
import com.example.Veco.global.enums.Priority;
import com.example.Veco.global.enums.State;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
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

    public NotiResDTO.GroupedNotiList<NotiResDTO.IssuePreViewDTO> toIssuePreviewListByState(List<NotiResDTO.IssuePreViewDTO> list, LocalDate deadline) {
        List<State> order = Arrays.asList(State.NONE, State.IN_PROGRESS, State.TODO);

        List<NotiResDTO.GroupedNotiList.NotiGroup<NotiResDTO.IssuePreViewDTO>> grouped = order.stream()
                .map(state -> {
                    List<NotiResDTO.IssuePreViewDTO> filtered = list.stream()
                            .filter(dto -> dto.getState() == state)
                            .sorted(Comparator.comparing(NotiResDTO.IssuePreViewDTO::getAlarmId).reversed()) 
                            .collect(Collectors.toList());

                    if (filtered.isEmpty()) return null;

                    return NotiResDTO.GroupedNotiList.NotiGroup.<NotiResDTO.IssuePreViewDTO>builder()
                            .groupTitle(state.name())
                            .notiList(filtered)
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return NotiResDTO.GroupedNotiList.<NotiResDTO.IssuePreViewDTO>builder()
                .type(Category.ISSUE)
                .deadline(deadline)
                .groupedList(grouped)
                .totalSize(list.size())
                .build();
    }

    public NotiResDTO.GroupedNotiList<NotiResDTO.IssuePreViewDTO> toIssuePreviewListByPriority(List<NotiResDTO.IssuePreViewDTO> list, LocalDate deadline) {
        List<Priority> order = Arrays.asList(Priority.NONE, Priority.URGENT, Priority.HIGH, Priority.NORMAL, Priority.LOW);

        List<NotiResDTO.GroupedNotiList.NotiGroup<NotiResDTO.IssuePreViewDTO>> grouped = order.stream()
                .map(priority -> {
                    List<NotiResDTO.IssuePreViewDTO> filtered = list.stream()
                            .filter(dto -> dto.getPriority() == priority)
                            .sorted(Comparator.comparing(NotiResDTO.IssuePreViewDTO::getAlarmId).reversed())
                            .collect(Collectors.toList());

                    if (filtered.isEmpty()) return null;

                    return NotiResDTO.GroupedNotiList.NotiGroup.<NotiResDTO.IssuePreViewDTO>builder()
                            .groupTitle(priority.name())
                            .notiList(filtered)
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return NotiResDTO.GroupedNotiList.<NotiResDTO.IssuePreViewDTO>builder()
                .type(Category.ISSUE)
                .deadline(deadline)
                .groupedList(grouped)
                .totalSize(list.size())
                .build();
    }

    // 목표

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

    public NotiResDTO.GroupedNotiList<NotiResDTO.GoalPreViewDTO> toGoalPreviewListByState(List<NotiResDTO.GoalPreViewDTO> list, LocalDate deadline) {
        List<State> order = Arrays.asList(State.NONE, State.IN_PROGRESS,State.TODO);

        List<NotiResDTO.GroupedNotiList.NotiGroup<NotiResDTO.GoalPreViewDTO>> grouped = order.stream()
                .map(state -> {
                    List<NotiResDTO.GoalPreViewDTO> filtered = list.stream()
                            .filter(dto -> dto.getState() == state)
                            .sorted(Comparator.comparing(NotiResDTO.GoalPreViewDTO::getAlarmId).reversed())
                            .collect(Collectors.toList());

                    if (filtered.isEmpty()) return null;

                    return NotiResDTO.GroupedNotiList.NotiGroup.<NotiResDTO.GoalPreViewDTO>builder()
                            .groupTitle(state.name())
                            .notiList(filtered)
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return NotiResDTO.GroupedNotiList.<NotiResDTO.GoalPreViewDTO>builder()
                .type(Category.GOAL)
                .deadline(deadline)
                .groupedList(grouped)
                .totalSize(list.size())
                .build();
    }

    public NotiResDTO.GroupedNotiList<NotiResDTO.GoalPreViewDTO> toGoalPreviewListByPriority(List<NotiResDTO.GoalPreViewDTO> list, LocalDate deadline) {
        List<Priority> order = Arrays.asList(Priority.NONE, Priority.URGENT, Priority.HIGH, Priority.NORMAL, Priority.LOW);

        List<NotiResDTO.GroupedNotiList.NotiGroup<NotiResDTO.GoalPreViewDTO>> grouped = order.stream()
                .map(priority -> {
                    List<NotiResDTO.GoalPreViewDTO> filtered = list.stream()
                            .filter(dto -> dto.getPriority() == priority)
                            .sorted(Comparator.comparing(NotiResDTO.GoalPreViewDTO::getAlarmId).reversed())
                            .collect(Collectors.toList());

                    if (filtered.isEmpty()) return null;

                    return NotiResDTO.GroupedNotiList.NotiGroup.<NotiResDTO.GoalPreViewDTO>builder()
                            .groupTitle(priority.name())
                            .notiList(filtered)
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return NotiResDTO.GroupedNotiList.<NotiResDTO.GoalPreViewDTO>builder()
                .type(Category.GOAL)
                .deadline(deadline)
                .groupedList(grouped)
                .totalSize(list.size())
                .build();
    }

}