package com.example.Veco.global.apiPayload.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum SuccessStatus {

    _OK(HttpStatus.OK, "COMMON200", "성공입니다.");

    private HttpStatus status;
    private String code;
    private String message;

}
