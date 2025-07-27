package com.example.Veco.domain.notification.exception;

import com.example.Veco.global.apiPayload.code.BaseErrorStatus;
import com.example.Veco.global.apiPayload.exception.VecoException;

public class NotificationException extends VecoException {

    public NotificationException(BaseErrorStatus code) {
        super(code);
    }

}