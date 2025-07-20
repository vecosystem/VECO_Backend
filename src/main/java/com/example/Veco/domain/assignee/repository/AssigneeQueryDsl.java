package com.example.Veco.domain.assignee.repository;

import com.example.Veco.domain.assignee.entity.Assignee;
import com.example.Veco.domain.mapping.MemberTeam;
import com.example.Veco.global.enums.Category;

import java.util.List;

public interface AssigneeQueryDsl {
    List<Assignee> findByMemberTeamsAndTypeAndTargetIds(List<MemberTeam> memberTeams, Category type, List<Long> targetIds);
}

