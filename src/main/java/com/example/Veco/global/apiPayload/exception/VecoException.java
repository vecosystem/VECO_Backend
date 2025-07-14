package com.example.Veco.global.apiPayload.exception;

import com.example.Veco.global.apiPayload.ErrorReasonDTO;
import com.example.Veco.global.apiPayload.code.ErrorStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VecoException extends RuntimeException {

    private ErrorStatus errorStatus;

    public VecoException(String message) {
        super(message);
    }

    public ErrorReasonDTO getErrorReasonHttpStatus() {
        return errorStatus.getReasonHttpStatus();
    }

}
