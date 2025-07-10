package com.example.Veco.domain.external.converter;

import com.example.Veco.domain.external.dto.ExternalRequestDTO;
import com.example.Veco.domain.external.dto.ExternalResponseDTO;
import com.example.Veco.domain.external.entity.External;
import com.example.Veco.domain.team.dto.AssigneeResponseDTO;

import java.util.List;

public class ExternalConverter {
    public static External toExternal(ExternalRequestDTO.ExternalCreateRequestDTO dto, String externalCode){
        return External.builder()
                .description(dto.getDescription())
                .deadline(dto.getDeadline())
                .external_code(externalCode)
                .type(dto.getExtServiceType())
                .priority(dto.getPriority())
                .title(dto.getTitle())
                .state(dto.getState())
                .build();
    }

    public static ExternalResponseDTO.ExternalDTO toExternalDTO(External external, List<AssigneeResponseDTO.AssigneeDTO> assignees){
        return ExternalResponseDTO.ExternalDTO.builder()
                .id(external.getId())
                .description(external.getDescription())
                .deadline(external.getDeadline())
                .externalCode(external.getExternal_code())
                .priority(external.getPriority())
                .title(external.getTitle())
                .state(external.getState())
                .assignees(assignees)
                .build();
    }
}
