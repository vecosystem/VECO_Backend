package com.example.Veco.domain.team.entity;

import com.example.Veco.global.apiPayload.code.BaseErrorStatus;
import com.example.Veco.global.apiPayload.exception.VecoException;

public class TeamException extends VecoException {
    public TeamException(BaseErrorStatus code) {
        super(code);
    }
}
