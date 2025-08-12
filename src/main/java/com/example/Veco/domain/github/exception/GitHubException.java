package com.example.Veco.domain.github.exception;

import com.example.Veco.global.apiPayload.code.BaseErrorStatus;
import com.example.Veco.global.apiPayload.exception.VecoException;

public class GitHubException extends VecoException {
    public GitHubException(BaseErrorStatus errorStatus) {
        super(errorStatus);
    }
}
