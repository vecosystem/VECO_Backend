package com.example.Veco.domain.team.repository;

import com.example.Veco.domain.team.entity.NumberSequence;
import com.example.Veco.domain.team.enums.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NumberSequenceRepository extends JpaRepository<NumberSequence, Long> {
    Optional<NumberSequence> findByTeamIdAndNumberType(Long teamId, Category category);
}
