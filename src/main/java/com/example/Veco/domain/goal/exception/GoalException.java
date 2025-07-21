package com.example.Veco.domain.goal.exception;

import com.example.Veco.global.apiPayload.code.BaseErrorStatus;
import com.example.Veco.global.apiPayload.exception.VecoException;

public class GoalException extends VecoException {

    public GoalException(BaseErrorStatus code) {
        super(code);
    }
}
