package com.example.Veco.domain.workspace.error;

import com.example.Veco.global.apiPayload.code.BaseErrorStatus;
import com.example.Veco.global.apiPayload.exception.VecoException;

public class WorkspaceHandler extends VecoException {

    public WorkspaceHandler(BaseErrorStatus errorStatus) {
        super(errorStatus);
    }
}
