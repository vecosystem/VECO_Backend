package com.example.Veco.domain.team.dto;

import com.example.Veco.global.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class NumberSequenceResponseDTO {

    private Long teamId;
    private Category category;
    private boolean isAllocated;
    private String nextCode;
}
