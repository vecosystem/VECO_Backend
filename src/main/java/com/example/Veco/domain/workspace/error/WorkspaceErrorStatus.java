package com.example.Veco.domain.workspace.error;

import com.example.Veco.global.apiPayload.ErrorReasonDTO;
import com.example.Veco.global.apiPayload.code.BaseErrorStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum WorkspaceErrorStatus implements BaseErrorStatus {
    _WORKSPACE_NOT_FOUND(HttpStatus.NOT_FOUND, "WORKSPACE4001", "해당 워크스페이스가 없습니다."),
    _WORKSPACE_DUPLICATED(HttpStatus.BAD_REQUEST, "WORKSPACE4002", "워크스페이스는 한 사람당 하나만 존재합니다.")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .isSuccess(false)
                .code(code)
                .message(message)
                .httpStatus(httpStatus)
                .build();
    }
}
