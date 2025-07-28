package com.example.Veco.domain.issue.converter;

import com.example.Veco.domain.goal.dto.response.GoalResDTO;
import com.example.Veco.domain.goal.entity.Goal;
import com.example.Veco.domain.issue.dto.IssueResponseDTO;

import java.util.List;

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
}
