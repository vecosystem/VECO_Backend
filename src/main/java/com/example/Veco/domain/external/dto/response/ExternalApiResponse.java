package com.example.Veco.domain.external.dto.response;

import com.example.Veco.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "외부 이슈 응답값")
public class ExternalApiResponse extends ApiResponse<ExternalResponseDTO.ExternalInfoDTO> {
    public ExternalApiResponse(boolean success, String code, String message, ExternalResponseDTO.ExternalInfoDTO result) {
        super(success, code, message, result);
    }
}
