package com.example.Veco.domain.team.converter;

import com.example.Veco.domain.mapping.Assignment;
import com.example.Veco.domain.team.dto.AssigneeResponseDTO;

public class AssigneeConverter {

    public static AssigneeResponseDTO.AssigneeDTO toAssigneeResponseDTO(Assignment assignment) {
        return AssigneeResponseDTO.AssigneeDTO.builder()
                .id(assignment.getId())
                .assigneeName(assignment.getAssigneeName())
                .profileUrl(assignment.getProfileUrl())
                .build();
    }
}
