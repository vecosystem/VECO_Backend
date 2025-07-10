package com.example.Veco.domain.external.service;

import com.example.Veco.domain.external.converter.ExternalConverter;
import com.example.Veco.domain.external.dto.ExternalRequestDTO;
import com.example.Veco.domain.external.dto.ExternalResponseDTO;
import com.example.Veco.domain.external.entity.External;
import com.example.Veco.domain.external.repository.ExternalRepository;
import com.example.Veco.domain.mapping.Assignee;
import com.example.Veco.domain.mapping.repository.AssigneeRepository;
import com.example.Veco.domain.team.converter.AssigneeConverter;
import com.example.Veco.domain.team.dto.AssigneeResponseDTO;
import com.example.Veco.domain.team.dto.NumberSequenceResponseDTO;
import com.example.Veco.domain.team.enums.Category;
import com.example.Veco.domain.team.service.NumberSequenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExternalService {

    private final ExternalRepository externalRepository;
    private final NumberSequenceService numberSequenceService;
    private final AssigneeRepository assigneeRepository;

    @Transactional
    public Long createExternal(ExternalRequestDTO.ExternalCreateRequestDTO request){

        NumberSequenceResponseDTO sequenceDTO = numberSequenceService
                .allocateNextNumber(request.getWorkSpaceName(), request.getTeamId(), Category.EXTERNAL);

        External external = ExternalConverter.toExternal(request, sequenceDTO.getNextCode());

        return externalRepository.save(external).getId();
    }

    public ExternalResponseDTO.ExternalDTO getExternalById(Long externalId) {

        List<Assignee> assignees = assigneeRepository.findByExternalId(externalId);

        External external = externalRepository.findById(externalId)
                .orElseThrow();

        List<AssigneeResponseDTO.AssigneeDTO> assigneeDTOS = assignees.stream()
                .map(AssigneeConverter::toAssigneeResponseDTO).toList();

        return ExternalConverter.toExternalDTO(external, assigneeDTOS);
    }


}
