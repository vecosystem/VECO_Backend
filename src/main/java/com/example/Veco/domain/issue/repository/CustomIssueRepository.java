package com.example.Veco.domain.issue.repository;

import com.example.Veco.domain.assignee.entity.Assignee;
import com.example.Veco.domain.issue.dto.IssueResponseDTO;
import com.example.Veco.domain.issue.entity.Issue;
import com.querydsl.core.types.Predicate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface CustomIssueRepository {
    List<IssueResponseDTO.SimpleIssue> findIssuesByTeamId(
            Predicate query,
            int size
    );

    Long findIssuesCountByFilter(
            Predicate query
    );

    List<String> findIssuesAssigneeInTeam (
            Long teamId
    );

    List<IssueResponseDTO.GoalInfo> findGoalInfoByTeamId(
            Long teamId
    );

    Map<Long, List<Assignee>> findManagerInfoByTeamId(
            Long teamId
    );

    List<Issue> findAllByTeamId(
            Long teamId
    );
}
