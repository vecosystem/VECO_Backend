package com.example.Veco.global.redis.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum RedisErrorCode{

    LOCK_TIMEOUT(HttpStatus.REQUEST_TIMEOUT,
            "REDIS408_0",
            "요청을 처리하는데 시간이 오래걸립니다.")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
