package com.example.Veco.domain.workspace.error;

import com.example.Veco.global.apiPayload.code.BaseErrorStatus;
import com.example.Veco.global.apiPayload.exception.VecoException;

public class SettingHandler extends VecoException {
    public SettingHandler(BaseErrorStatus errorStatus) {
        super(errorStatus);
    }
}
