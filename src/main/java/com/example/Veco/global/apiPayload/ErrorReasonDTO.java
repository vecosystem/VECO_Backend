package com.example.Veco.global.apiPayload;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
public class ErrorReasonDTO {
    private HttpStatus httpStatus;
    private boolean isSuccess;
    private String message;
    private String code;

}
