package com.example.Veco.domain.notification.converter;

import com.example.Veco.domain.assignee.entity.Assignee;
import com.example.Veco.domain.external.entity.External;
import com.example.Veco.domain.goal.entity.Goal;
import com.example.Veco.domain.issue.entity.Issue;
import com.example.Veco.domain.memberNotification.entity.MemberNotification;
import com.example.Veco.domain.notification.dto.NotiResDTO;
import com.example.Veco.domain.notification.dto.NotiResDTO.*;
import com.example.Veco.global.enums.Category;
import com.example.Veco.global.enums.ExtServiceType;
import com.example.Veco.global.enums.Priority;
import com.example.Veco.global.enums.State;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class NotiConverter {

    private <T, K extends Enum<K>> List<GroupedNotiList.NotiGroup<T>> groupByEnum(
            List<T> list,
            Function<T, K> classifier,
            List<K> enumOrder,
            Function<T, Long> alarmIdExtractor
    ) {
        return enumOrder.stream()
                .map(enumValue -> {
                    List<T> filtered = list.stream()
                            .filter(item -> classifier.apply(item) == enumValue)
                            .sorted(Comparator.comparing(alarmIdExtractor).reversed())
                            .collect(Collectors.toList());

                    if (filtered.isEmpty()) return null;

                    return GroupedNotiList.NotiGroup.<T>builder()
                            .groupTitle(enumValue.name())
                            .notiList(filtered)
                            .build();
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private <T> List<GroupedNotiList.NotiGroup<T>> groupByStringField(
            List<T> list,
            Function<T, String> classifier,
            Function<T, Long> alarmIdExtractor
    ) {
        return list.stream()
                .collect(Collectors.groupingBy(classifier))
                .entrySet().stream()
                .map(entry -> {
                    List<T> sorted = entry.getValue().stream()
                            .sorted(Comparator.comparing(alarmIdExtractor).reversed())
                            .collect(Collectors.toList());

                    return GroupedNotiList.NotiGroup.<T>builder()
                            .groupTitle(entry.getKey())
                            .notiList(sorted)
                            .build();
                })
                .sorted(Comparator.comparing(
                        GroupedNotiList.NotiGroup::getGroupTitle,
                        Comparator.nullsFirst(Comparator.naturalOrder())
                ))
                .collect(Collectors.toList());
    }

    private <T> GroupedNotiList<T> buildGroupedList(Category category, LocalDate deadline,
                                                    List<GroupedNotiList.NotiGroup<T>> groupedList, int totalSize) {
        return GroupedNotiList.<T>builder()
                .type(category)
                .deadline(deadline)
                .groupedList(groupedList)
                .totalSize(totalSize)
                .build();
    }

    private List<ManagerInfo> toManagerInfoList(List<Assignee> assignees){
        if (assignees == null) return Collections.emptyList();
        return assignees.stream()
                .map(assignee -> {
                    return NotiResDTO.ManagerInfo.builder()
                            .name(assignee.getMemberTeam().getMember().getName())
                            .profileUrl(assignee.getMemberTeam().getMember().getProfile().getProfileImageUrl())
                            .build();
                })
                .collect(Collectors.toList());
    }

    // Issue DTO 리스트 반환
    public IssuePreViewDTO toIssuePreViewDTO(Issue issue, MemberNotification memberNoti, List<Assignee> assignees) {
        return NotiResDTO.IssuePreViewDTO.builder()
                .alarmId(memberNoti.getId())
                .name(issue.getName())
                .typeId(issue.getId())
                .teamId(issue.getTeam().getId())
                .title(issue.getTitle())
                .state(issue.getState())
                .priority(issue.getPriority())
                .goalTitle(issue.getGoal().getTitle())
                .managerList(toManagerInfoList(assignees))
                .isRead(memberNoti.getIsRead())
                .build();
    }
    public List<IssuePreViewDTO> toIssuePreviewDTOs(
            List<Issue> issueList,
            Map<Long, MemberNotification> notiMap,
            Map<Long, List<Assignee>> assigneeMap
    ) {
        return issueList.stream()
                .map(issue -> toIssuePreViewDTO(
                        issue,
                        notiMap.get(issue.getId()),
                        assigneeMap.getOrDefault(issue.getId(), Collections.emptyList())
                ))
                .collect(Collectors.toList());
    }


    // Issue 그룹핑
    public GroupedNotiList<IssuePreViewDTO> toIssuePreviewListByState(List<IssuePreViewDTO> list, LocalDate deadline) {
        List<State> order = Arrays.asList(State.NONE, State.IN_PROGRESS, State.TODO);
        return buildGroupedList(Category.ISSUE, deadline, groupByEnum(list, IssuePreViewDTO::getState, order, IssuePreViewDTO::getAlarmId), list.size());
    }
    public GroupedNotiList<IssuePreViewDTO> toIssuePreviewListByPriority(List<IssuePreViewDTO> list, LocalDate deadline) {
        List<Priority> order = Arrays.asList(Priority.NONE, Priority.URGENT, Priority.HIGH, Priority.NORMAL, Priority.LOW);
        return buildGroupedList(Category.ISSUE, deadline, groupByEnum(list, IssuePreViewDTO::getPriority, order, IssuePreViewDTO::getAlarmId), list.size());
    }
    public GroupedNotiList<IssuePreViewDTO> toIssuePreviewListByGoal(List<IssuePreViewDTO> list, LocalDate deadline) {
        return buildGroupedList(Category.ISSUE, deadline, groupByStringField(list, IssuePreViewDTO::getGoalTitle, IssuePreViewDTO::getAlarmId), list.size());
    }

    // Goal DTO 리스트 반환
    public GoalPreViewDTO toGoalPreViewDTO(Goal goal, MemberNotification memberNoti, List<Assignee> assignees) {

        return GoalPreViewDTO.builder()
                .alarmId(memberNoti.getId())
                .name(goal.getName())
                .typeId(goal.getId())
                .teamId(goal.getTeam().getId())
                .title(goal.getTitle())
                .state(goal.getState())
                .priority(goal.getPriority())
                .managerList(toManagerInfoList(assignees))
                .isRead(memberNoti.getIsRead())
                .build();
    }
    public List<GoalPreViewDTO> toGoalPreviewDTOs(
            List<Goal> list,
            Map<Long, MemberNotification> notiMap,
            Map<Long, List<Assignee>> assigneeMap
    ) {
        return list.stream()
                .map(goal -> toGoalPreViewDTO(
                        goal,
                        notiMap.get(goal.getId()),
                        assigneeMap.getOrDefault(goal.getId(), Collections.emptyList())
                ))
                .collect(Collectors.toList());
    }

    // Goal 그룹핑
    public GroupedNotiList<GoalPreViewDTO> toGoalPreviewListByState(List<GoalPreViewDTO> list, LocalDate deadline) {
        List<State> order = Arrays.asList(State.NONE, State.IN_PROGRESS, State.TODO);
        return buildGroupedList(Category.GOAL, deadline, groupByEnum(list, GoalPreViewDTO::getState, order, GoalPreViewDTO::getAlarmId), list.size());
    }
    public GroupedNotiList<GoalPreViewDTO> toGoalPreviewListByPriority(List<GoalPreViewDTO> list, LocalDate deadline) {
        List<Priority> order = Arrays.asList(Priority.NONE, Priority.URGENT, Priority.HIGH, Priority.NORMAL, Priority.LOW);
        return buildGroupedList(Category.GOAL, deadline, groupByEnum(list, GoalPreViewDTO::getPriority, order, GoalPreViewDTO::getAlarmId), list.size());
    }


    // External DTO 리스트 반환
    public ExternalPreViewDTO toExternalPreViewDTO(External external, MemberNotification memberNoti,  List<Assignee> assignees) {

        return ExternalPreViewDTO.builder()
                .alarmId(memberNoti.getId())
                .name(external.getName())
                .typeId(external.getId())
                .teamId(external.getTeam().getId())
                .title(external.getTitle())
                .state(external.getState())
                .priority(external.getPriority())
                .goalTitle(external.getGoal().getTitle())
                .managerList(toManagerInfoList(assignees))
                .externalTool(external.getType())
                .isRead(memberNoti.getIsRead())
                .build();
    }
    public List<ExternalPreViewDTO> toExternalPreviewDTOs(
            List<External> list,
            Map<Long, MemberNotification> notiMap,
            Map<Long, List<Assignee>> assigneeMap
    ) {
        return list.stream()
                .map(external -> toExternalPreViewDTO(
                        external,
                        notiMap.get(external.getId()),
                        assigneeMap.getOrDefault(external.getId(), Collections.emptyList())
                ))
                .collect(Collectors.toList());
    }

    // External 그룹핑
    public GroupedNotiList<ExternalPreViewDTO> toExternalPreviewListByState(List<ExternalPreViewDTO> list, LocalDate deadline) {
        List<State> order = Arrays.asList(State.NONE, State.IN_PROGRESS, State.TODO);
        return buildGroupedList(Category.EXTERNAL, deadline, groupByEnum(list, ExternalPreViewDTO::getState, order, ExternalPreViewDTO::getAlarmId), list.size());
    }
    public GroupedNotiList<ExternalPreViewDTO> toExternalPreviewListByPriority(List<ExternalPreViewDTO> list, LocalDate deadline) {
        List<Priority> order = Arrays.asList(Priority.NONE, Priority.URGENT, Priority.HIGH, Priority.NORMAL, Priority.LOW);
        return buildGroupedList(Category.EXTERNAL, deadline, groupByEnum(list, ExternalPreViewDTO::getPriority, order, ExternalPreViewDTO::getAlarmId), list.size());
    }
    public GroupedNotiList<ExternalPreViewDTO> toExternalPreviewListByGoal(List<ExternalPreViewDTO> list, LocalDate deadline) {
        return buildGroupedList(Category.EXTERNAL, deadline, groupByStringField(list, ExternalPreViewDTO::getGoalTitle, ExternalPreViewDTO::getAlarmId), list.size());
    }
    public GroupedNotiList<ExternalPreViewDTO> toExternalPreviewListByExternal(List<ExternalPreViewDTO> list, LocalDate deadline) {
        List<ExtServiceType> order = Arrays.asList(ExtServiceType.NONE, ExtServiceType.SLACK, ExtServiceType.GITHUB, ExtServiceType.NOTION);
        return buildGroupedList(Category.EXTERNAL, deadline, groupByEnum(list, ExternalPreViewDTO::getExternalTool, order, ExternalPreViewDTO::getAlarmId), list.size());
    }

}