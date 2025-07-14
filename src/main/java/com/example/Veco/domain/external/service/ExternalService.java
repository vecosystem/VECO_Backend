package com.example.Veco.domain.external.service;

import com.example.Veco.domain.external.converter.ExternalConverter;
import com.example.Veco.domain.external.dto.ExternalRequestDTO;
import com.example.Veco.domain.external.dto.ExternalResponseDTO;
import com.example.Veco.domain.external.dto.ExternalSearchCriteria;
import com.example.Veco.domain.external.entity.External;
import com.example.Veco.domain.external.repository.ExternalRepository;
import com.example.Veco.domain.mapping.Assignment;
import com.example.Veco.domain.mapping.repository.AssigneeRepository;
import com.example.Veco.domain.team.converter.AssigneeConverter;
import com.example.Veco.domain.team.dto.AssigneeResponseDTO;
import com.example.Veco.domain.team.dto.NumberSequenceResponseDTO;
import com.example.Veco.domain.team.enums.Category;
import com.example.Veco.domain.team.service.NumberSequenceService;
import com.example.Veco.global.apiPayload.code.ErrorStatus;
import com.example.Veco.global.apiPayload.exception.VecoException;
import com.example.Veco.global.apiPayload.page.CursorPage;
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

        List<Assignment> assignments = assigneeRepository.findByExternalId(externalId);

        External external = externalRepository.findById(externalId)
                .orElseThrow();

        List<AssigneeResponseDTO.AssigneeDTO> assigneeDTOS = assignments.stream()
                .map(AssigneeConverter::toAssigneeResponseDTO).toList();

        return ExternalConverter.toExternalDTO(external, assigneeDTOS);
    }

//    public CursorPage<External> getExternalsWithPagenation(ExternalSearchCriteria criteria, String cursor, int size){
//        CursorPage<External> externalWithCursor = externalRepository.findExternalWithCursor(criteria, cursor, size);
//        return externalWithCursor;
//    }

    @Transactional
    public void deleteExternals(ExternalRequestDTO.ExternalDeleteRequestDTO request) {
        externalRepository.deleteByExternalId(request.getExternalIds());
    }

    @Transactional
    public void updateExternal(Long externalId, ExternalRequestDTO.ExternalUpdateRequestDTO request) {
        External external = externalRepository.findById(externalId)
                .orElseThrow(() -> new VecoException(ErrorStatus.EXTERNAL_NOT_FOUND));

        external.updateExternal(request);


    }
}
