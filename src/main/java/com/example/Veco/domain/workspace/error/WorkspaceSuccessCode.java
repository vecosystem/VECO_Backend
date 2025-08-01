package com.example.Veco.domain.workspace.error;

import com.example.Veco.global.apiPayload.code.BaseSuccessStatus;
import com.example.Veco.global.apiPayload.dto.SuccessReasonDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum WorkspaceSuccessCode implements BaseSuccessStatus {

    OK(HttpStatus.OK,
            "WORK200",
            "정상적으로 처리했습니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;

    @Override
    public SuccessReasonDTO getReasonHttpStatus() {
        return SuccessReasonDTO.builder()
                .isSuccess(true)
                .httpStatus(status)
                .code(code)
                .message(message)
                .build();
    }
}
