package com.example.Veco.global.auth.user.exception;

import com.example.Veco.global.apiPayload.code.BaseErrorStatus;
import com.example.Veco.global.apiPayload.exception.VecoException;

public class UserException extends VecoException {

    public UserException(BaseErrorStatus errorStatus) {
        super(errorStatus);
    }
}
