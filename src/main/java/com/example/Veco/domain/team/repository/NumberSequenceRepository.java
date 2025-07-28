package com.example.Veco.domain.team.repository;

import com.example.Veco.domain.team.entity.NumberSequence;
import com.example.Veco.domain.team.enums.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface NumberSequenceRepository extends JpaRepository<NumberSequence, Long> {

    @Query("select n from NumberSequence n where n.team.id = :teamId and n.category = :category")
    Optional<NumberSequence> findByTeamIdAndNumberType(@Param("teamId") Long teamId, @Param("category") Category category);
}
