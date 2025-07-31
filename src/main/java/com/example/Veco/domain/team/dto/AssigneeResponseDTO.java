package com.example.Veco.domain.team.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AssigneeResponseDTO {



    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AssigneeDTO {
        private String assigneeName;
        private String profileUrl;
    }
}
