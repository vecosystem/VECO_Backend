package com.example.Veco.domain.external.exception;

import com.example.Veco.global.apiPayload.code.BaseErrorStatus;
import com.example.Veco.global.apiPayload.exception.VecoException;

public class ExternalException extends VecoException {
    public ExternalException(BaseErrorStatus status) {
        super(status);
    }
}
