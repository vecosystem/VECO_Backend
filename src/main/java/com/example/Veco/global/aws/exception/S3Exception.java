package com.example.Veco.global.aws.exception;

import com.example.Veco.global.apiPayload.code.BaseErrorStatus;
import com.example.Veco.global.apiPayload.exception.VecoException;

public class S3Exception extends VecoException {
    public S3Exception(BaseErrorStatus code) {
        super(code);
    }
}
