package com.example.Veco.domain.goal.repository;

import com.example.Veco.domain.goal.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long>, GoalQueryDsl {

    List<Goal> findAllByTeamId(Long teamId);
    List<Goal> findByIdIn(List<Long> goalIds);

    // 삭제된 목표 조회
    @Query(value = "SELECT * FROM goal WHERE deleted_at IS NOT NULL AND team_id =:teamId", nativeQuery = true)
    List<Goal> findAllByTeamIdAndDeleted(Long teamId);

    // 삭제된 목표 ID로 조회
    @Query(value = "SELECT * FROM goal WHERE deleted_at IS NOT NULL AND id IN :goalIds", nativeQuery = true)
    List<Goal> findDeletedGoalsById(List<Long> goalIds);
}
