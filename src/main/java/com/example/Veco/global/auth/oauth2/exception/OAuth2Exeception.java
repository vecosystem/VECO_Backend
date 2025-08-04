package com.example.Veco.global.auth.oauth2.exception;

import com.example.Veco.global.apiPayload.code.BaseErrorStatus;
import com.example.Veco.global.apiPayload.exception.VecoException;

public class OAuth2Exeception extends VecoException {
    public OAuth2Exeception (BaseErrorStatus code) {
        super(code);
    }
}
