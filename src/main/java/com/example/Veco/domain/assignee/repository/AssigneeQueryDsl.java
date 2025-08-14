package com.example.Veco.domain.assignee.repository;

import com.example.Veco.domain.assignee.entity.Assignee;
import com.example.Veco.domain.mapping.entity.MemberTeam;
import com.example.Veco.global.enums.Category;

import java.util.List;

public interface AssigneeQueryDsl {
    List<Assignee> findByMemberTeamsAndTypeAndTargetIds(List<MemberTeam> memberTeams, Category type, List<Long> targetIds);
    List<Assignee> findByIssueIdIn(List<Long> issueIds);
    List<Assignee> findByGoalIdIn(List<Long> goalIds);
    List<Assignee> findByExternalIdIn(List<Long> externalIds);
    void deleteAllByTypeAndTargetIds(Category type, List<Long> targetIds);
    List<Assignee> findByTypeAndTargetId(Category type, Long targetId);
}

