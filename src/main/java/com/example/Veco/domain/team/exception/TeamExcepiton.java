package com.example.Veco.domain.team.exception;

import com.example.Veco.global.apiPayload.code.BaseErrorStatus;
import com.example.Veco.global.apiPayload.exception.VecoException;

public class TeamExcepiton extends VecoException {
    public TeamExcepiton(BaseErrorStatus code) {
        super(code);
    }
}
