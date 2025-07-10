package com.example.Veco.domain.goal.exception;

import com.example.Veco.global.apiPayload.exception.VecoException;

public class GoalException extends VecoException {
    public GoalException(String code) {
        super(code);
    }
}
