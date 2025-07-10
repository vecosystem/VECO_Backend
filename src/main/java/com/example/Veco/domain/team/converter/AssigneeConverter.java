package com.example.Veco.domain.team.converter;

import com.example.Veco.domain.mapping.Assignee;
import com.example.Veco.domain.team.dto.AssigneeResponseDTO;

public class AssigneeConverter {

    public static AssigneeResponseDTO.AssigneeDTO toAssigneeResponseDTO(Assignee assignee) {
        return AssigneeResponseDTO.AssigneeDTO.builder()
                .id(assignee.getId())
                .assigneeName(assignee.getAssigneeName())
                .profileUrl(assignee.getProfileUrl())
                .build();
    }
}
