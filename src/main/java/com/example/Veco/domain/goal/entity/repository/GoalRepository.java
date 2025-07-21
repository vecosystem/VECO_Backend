package com.example.Veco.domain.goal.entity.repository;

import com.example.Veco.domain.goal.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<Goal, Long> {
}
