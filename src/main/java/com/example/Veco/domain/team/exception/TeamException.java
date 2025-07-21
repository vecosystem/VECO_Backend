package com.example.Veco.domain.team.exception;

import com.example.Veco.global.apiPayload.code.BaseErrorStatus;
import com.example.Veco.global.apiPayload.exception.VecoException;

public class TeamException extends VecoException {
    public TeamException(BaseErrorStatus errorStatus) {
        super(errorStatus);
    public TeamException(BaseErrorStatus code) {
        super(code);
    }
}
