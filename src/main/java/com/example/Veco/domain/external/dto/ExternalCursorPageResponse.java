package com.example.Veco.domain.external.dto;

import com.example.Veco.global.apiPayload.ApiResponse;
import com.example.Veco.global.apiPayload.page.CursorPage;

public class ExternalCursorPageResponse extends ApiResponse<CursorPage<ExternalResponseDTO.ExternalDTO>> {
    public ExternalCursorPageResponse(boolean success, String code, String message, CursorPage<ExternalResponseDTO.ExternalDTO> result) {
        super(success, code, message, result);
    }
}
