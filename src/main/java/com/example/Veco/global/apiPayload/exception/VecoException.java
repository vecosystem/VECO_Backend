package com.example.Veco.global.apiPayload.exception;

import com.example.Veco.global.apiPayload.ErrorReasonDTO;
import com.example.Veco.global.apiPayload.code.BaseErrorStatus;
import com.example.Veco.global.apiPayload.code.ErrorStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import com.example.Veco.global.apiPayload.code.BaseErrorStatus;

@Getter
public class VecoException extends RuntimeException {

    private final BaseErrorStatus errorStatus;

    public VecoException(BaseErrorStatus errorStatus) {
        this.errorStatus = errorStatus;
    }

    public ErrorReasonDTO getErrorReasonHttpStatus() {
        return errorStatus.getReasonHttpStatus();
    }

}
