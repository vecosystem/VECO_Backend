package com.example.Veco.domain.memberNotification.exception;

import com.example.Veco.global.apiPayload.code.BaseErrorStatus;
import com.example.Veco.global.apiPayload.exception.VecoException;

public class MemberNotiException extends VecoException {

    public MemberNotiException(BaseErrorStatus code) {
        super(code);
    }

}