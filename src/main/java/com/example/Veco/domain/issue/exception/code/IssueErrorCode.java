package com.example.Veco.domain.issue.exception.code;

import com.example.Veco.global.apiPayload.ErrorReasonDTO;
import com.example.Veco.global.apiPayload.code.BaseErrorStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum IssueErrorCode implements BaseErrorStatus {

    NOT_FOUND(HttpStatus.NOT_FOUND,
            "ISSUE404_0",
            "해당 목표가 존재하지 않습니다."),
    NOT_FOUND_IN_TEAM(HttpStatus.NOT_FOUND,
            "ISSUE404_1",
            "해당 팀에 목표가 존재하지 않습니다."),
    CURSOR_INVALID(HttpStatus.BAD_REQUEST,
            "ISSUE400_0",
            "유효하지 않은 커서입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .isSuccess(false)
                .httpStatus(httpStatus)
                .code(code)
                .message(message)
                .build();
    }
}
