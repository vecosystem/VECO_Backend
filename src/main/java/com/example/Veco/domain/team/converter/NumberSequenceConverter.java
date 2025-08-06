package com.example.Veco.domain.team.converter;

import com.example.Veco.domain.team.dto.NumberSequenceResponseDTO;
import com.example.Veco.domain.team.entity.NumberSequence;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.global.enums.Category;

public class NumberSequenceConverter {

    public static NumberSequence toNumberSequence(Team team, Category category) {
        return NumberSequence.builder()
                .team(team)
                .category(category)
                .currentNumber(0L)
                .build();
    }

    public static NumberSequenceResponseDTO toResponseDTO(Long teamId, Category category, String nextCode, boolean isAllocated) {
        return NumberSequenceResponseDTO.builder()
                .teamId(teamId)
                .category(category)
                .nextCode(nextCode)
                .isAllocated(isAllocated)
                .build();
    }
}
