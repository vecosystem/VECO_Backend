package com.example.Veco.domain.slack.exception;

import com.example.Veco.global.apiPayload.code.BaseErrorStatus;
import com.example.Veco.global.apiPayload.exception.VecoException;

public class SlackException extends VecoException {
    public SlackException(BaseErrorStatus errorStatus) {
        super(errorStatus);
    }
}
