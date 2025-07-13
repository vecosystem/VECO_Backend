package com.example.Veco.global.apiPayload.exception;

import com.example.Veco.global.apiPayload.ErrorReasonDTO;
import com.example.Veco.global.apiPayload.code.BaseErrorStatus;

public class VecoException extends RuntimeException {

    private final BaseErrorStatus errorStatus;

    public VecoException(BaseErrorStatus errorStatus) {this.errorStatus = errorStatus;}
    public ErrorReasonDTO getErrorReasonHttpStatus() {
        return this.errorStatus.getReasonHttpStatus();
    }
}
