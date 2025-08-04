package com.example.Veco.global.auth.jwt.exception;

import com.example.Veco.global.apiPayload.code.BaseErrorStatus;
import com.example.Veco.global.apiPayload.exception.VecoException;

public class CustomJwtException extends VecoException {

    public CustomJwtException(BaseErrorStatus code) {
        super(code);
    }
}
