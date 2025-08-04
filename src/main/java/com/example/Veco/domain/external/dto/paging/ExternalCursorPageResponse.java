package com.example.Veco.domain.external.dto.paging;

import com.example.Veco.domain.external.dto.response.ExternalResponseDTO;
import com.example.Veco.global.apiPayload.ApiResponse;
import com.example.Veco.global.apiPayload.page.CursorPage;

public class ExternalCursorPageResponse extends ApiResponse<CursorPage<ExternalResponseDTO.ExternalInfoDTO>> {
    public ExternalCursorPageResponse(boolean success, String code, String message, CursorPage<ExternalResponseDTO.ExternalInfoDTO> result) {
        super(success, code, message, result);
    }
}
