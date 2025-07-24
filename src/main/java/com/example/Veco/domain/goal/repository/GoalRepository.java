package com.example.Veco.domain.goal.repository;

import com.example.Veco.domain.goal.entity.Goal;
import com.example.Veco.domain.team.entity.Team;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long>, GoalQueryDsl {
    List<Goal> team(Team team);

    List<Goal> findAllByTeamId(Long teamId);
    List<Goal> findByIdIn(List<Long> goalIds);
}
