package com.example.Veco.domain.issue.entity;

import com.example.Veco.domain.goal.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IssueRepository extends JpaRepository<Issue, Long> {

    void deleteAllByGoalId(Long id);

    Optional<List<Issue>> findAllByGoal(Goal goal);
}
