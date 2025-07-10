package com.example.Veco.global.redis.exception;

import com.example.Veco.global.apiPayload.exception.VecoException;

public class RedisException extends VecoException {
    public RedisException(String code) {
        super(code);
    }
}
