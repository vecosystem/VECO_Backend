package com.example.Veco.domain.issue.exception;

import com.example.Veco.global.apiPayload.code.BaseErrorStatus;
import com.example.Veco.global.apiPayload.exception.VecoException;

public class IssueException extends VecoException {
    public IssueException(BaseErrorStatus code) {
        super(code);
    }
}
