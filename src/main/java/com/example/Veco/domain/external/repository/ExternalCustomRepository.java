package com.example.Veco.domain.external.repository;

import com.example.Veco.domain.external.dto.ExternalResponseDTO;
import com.example.Veco.domain.external.dto.ExternalSearchCriteria;
import com.example.Veco.domain.external.entity.External;
import com.example.Veco.global.apiPayload.page.CursorPage;

public interface ExternalCustomRepository {
    CursorPage<ExternalResponseDTO.ExternalDTO> findExternalWithCursor(ExternalSearchCriteria criteria, String cursor, int size);
}
