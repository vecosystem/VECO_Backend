package com.example.Veco.domain.issue.converter;

import com.example.Veco.domain.assignee.entity.Assignee;
import com.example.Veco.domain.comment.entity.Comment;
import com.example.Veco.domain.goal.entity.Goal;
import com.example.Veco.domain.issue.entity.Issue;
import com.example.Veco.domain.issue.dto.IssueResponseDTO;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@Slf4j
public class IssueConverter {
    // 팀 내 이슈 모두 조회: List<T>, filterName, dataCnt -> FilteringIssue
    public static <T> IssueResponseDTO.FilteringIssue<T> toFilteringIssue(
            List<T> simpleIssues,
            String filterName,
            Integer dataCnt
    ) {
        return IssueResponseDTO.FilteringIssue.<T>builder()
                .filterName(filterName)
                .dataCnt(dataCnt)
                .issues(simpleIssues)
                .build();
    }

    public static <T> IssueResponseDTO.Pageable<T> toPageable(
            List<T> data,
            boolean hasNext,
            String nextCursor,
            Integer pageSize
    ) {
        return IssueResponseDTO.Pageable.<T>builder()
                .data(data)
                .hasNext(hasNext)
                .nextCursor(nextCursor)
                .pageSize(pageSize)
                .build();
    }

    public static IssueResponseDTO.IssueWithManagers toIssueWithManagers(
            IssueResponseDTO.SimpleIssue simpleIssue, List<IssueResponseDTO.ManagerInfo> managerInfos
    ) {
        return IssueResponseDTO.IssueWithManagers.builder()
                .id(simpleIssue.id())
                .name(simpleIssue.name())
                .title(simpleIssue.title())
                .state(simpleIssue.state())
                .priority(simpleIssue.priority())
                .deadline(simpleIssue.deadline())
                .goal(simpleIssue.goal())
                .managers(managerInfos)
                .build();
    }

    public static IssueResponseDTO.DetailIssue toDetailIssue(
            Issue issue,
            List<IssueResponseDTO.SimpleManagerInfo> assignees,
            Goal goal,
            List<IssueResponseDTO.CommentInfo> comments
    ) {
        return IssueResponseDTO.DetailIssue.builder()
                .id(issue.getId())
                .name(issue.getName())
                .title(issue.getTitle())
                .content(issue.getContent())
                .state(issue.getState())
                .priority(issue.getPriority())
                .deadline(toDeadline(issue.getDeadlineStart(), issue.getDeadlineEnd()))
                .goal(toGoalInfo(goal))
                .managers(toData(assignees))
                .comments(toData(comments))
                .build();
    }

    public static IssueResponseDTO.Deadline toDeadline(
            LocalDate start,
            LocalDate end
    ) {
        return IssueResponseDTO.Deadline.builder()
                .start(start)
                .end(end)
                .build();
    }

    public static IssueResponseDTO.GoalInfo toGoalInfo(
            Goal goal
    ) {
        return IssueResponseDTO.GoalInfo.builder()
                .id(goal.getId())
                .title(goal.getTitle())
                .build();
    }

    public static List<IssueResponseDTO.SimpleManagerInfo> toSimpleManagerInfos(
            List<Assignee> assignees
    ) {
        return assignees.stream()
                .map(IssueConverter::toSimpleManagerInfo)
                .toList();
    }

    public static IssueResponseDTO.SimpleManagerInfo toSimpleManagerInfo(
            Assignee assignee
    ) {
        return IssueResponseDTO.SimpleManagerInfo.builder()
                .profileUrl(assignee.getMemberTeam().getMember().getProfile().getProfileImageUrl())
                .name(assignee.getMemberTeam().getMember().getName())
                .build();
    }


    public static IssueResponseDTO.CommentInfo toCommentInfo(
            Comment comment
    ) {
        return IssueResponseDTO.CommentInfo.builder()
                .name(comment.getMember().getName())
                .profileUrl(comment.getMember().getProfile().getProfileImageUrl())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .build();
    }

    public static List<IssueResponseDTO.CommentInfo> toCommentInfos(
            List<Comment> comments
    ) {
        if (comments == null || comments.isEmpty()) {
            return Collections.emptyList();
        }
        return comments.stream()
                .map(IssueConverter::toCommentInfo)
                .toList();
    }

    public static <T> IssueResponseDTO.Data<T> toData(
            List<T> info
    ) {
        return IssueResponseDTO.Data.<T>builder()
                .cnt(info.size())
                .info(info)
                .build();
    }
}
