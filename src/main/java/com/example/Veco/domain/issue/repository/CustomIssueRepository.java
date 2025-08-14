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

    List<IssueResponseDTO.SimpleIssue> findUnassignedIssuesByTeamId(
            Long teamId,
            Predicate query,
            int size
    );

    Long findIssuesCountByFilter(
            Predicate query
    );

    Long findUnassignedIssuesCountByTeamId(
            Long teamId
    );

    List<String> findIssuesAssigneeInTeam(
            Long teamId
    );

    Long findNoGoalIssuesCountByTeamId(
            Long teamId
    );

    List<String> findGoalsByTeamId(
            Long teamId
    );

    Map<Long, List<Assignee>> findManagerInfoByTeamId(
            Long teamId
    );

    List<Issue> findAllByTeamId(
            Long teamId
    );
}
