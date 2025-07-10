package com.example.Veco.domain.external.dto;

import com.example.Veco.domain.team.dto.AssigneeResponseDTO;
import com.example.Veco.global.enums.Priority;
import com.example.Veco.global.enums.State;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class ExternalResponseDTO {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ExternalDTO {
        private Long id;
        private String title;
        private String description;
        private Priority priority;
        private State state;
        private LocalDate deadline;
        private String goalId;
        private String goalTitle;
        private String externalCode;
        private List<AssigneeResponseDTO.AssigneeDTO> assignees;
    }
}
