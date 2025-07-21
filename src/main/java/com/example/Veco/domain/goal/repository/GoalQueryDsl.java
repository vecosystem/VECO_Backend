package com.example.Veco.domain.goal.repository;


import com.example.Veco.domain.goal.dto.response.GoalResDTO;
import com.querydsl.core.types.Predicate;

import java.util.List;

public interface GoalQueryDsl {
    // 팀 내 모든 목표 조회
    List<GoalResDTO.SimpleGoal> findGoalsByTeamId(
            Predicate query,
            int size
    );

    // 필터에 맞는 모든 목표 개수 조회
    Long findGoalsCountByFilter(
            Predicate query
    );

    // 모든 목표 담당자 리스트 조회
    List<String> findGoalsAssigneeInTeam(
            Long teamId
    );
}
