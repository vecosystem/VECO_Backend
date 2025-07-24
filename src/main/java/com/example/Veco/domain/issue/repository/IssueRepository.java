package com.example.Veco.domain.issue.repository;

import com.example.Veco.domain.goal.entity.Goal;
import com.example.Veco.domain.issue.entity.Issue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface IssueRepository extends JpaRepository<Issue, Long> {
    List<Issue> findByIdIn(List<Long> issueIds);

    void deleteAllByGoalId(Long id);

    Optional<List<Issue>> findAllByGoal(Goal goal);
}
