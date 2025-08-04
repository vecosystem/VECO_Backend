package com.example.Veco.global.auth.oauth2.exception.code;

import com.example.Veco.global.apiPayload.ErrorReasonDTO;
import com.example.Veco.global.apiPayload.code.BaseErrorStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum OAuth2ErrorCode implements BaseErrorStatus {
    OAUTH2_INVALID_STATE(
            HttpStatus.BAD_REQUEST,
            "OAUTH2401_0",
            "state 값이 유효하지 않습니다."
    );

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
