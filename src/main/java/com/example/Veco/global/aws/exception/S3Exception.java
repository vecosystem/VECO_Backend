package com.example.Veco.global.aws.exception;

import com.example.Veco.global.aws.exception.code.S3ErrorCode;

public class S3Exception extends RuntimeException {
    public S3Exception(S3ErrorCode message) {
        super(message.getMessage());
    }
}
