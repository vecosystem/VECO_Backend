package com.example.Veco.domain.goal.exception.code;

import com.example.Veco.global.apiPayload.ErrorReasonDTO;
import com.example.Veco.global.apiPayload.code.BaseErrorStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum GoalErrorCode implements BaseErrorStatus {

    FORBIDDEN(HttpStatus.FORBIDDEN,
            "GOAL403_0",
            "권한이 없습니다."),
    NOT_FOUND_IN_TEAM(HttpStatus.NOT_FOUND,
            "GOAL404_0",
            "해당 팀에 목표가 존재하지 않습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND,
            "GOAL404_1",
            "해당 목표가 존재하지 않습니다."),
    CURSOR_INVALID(HttpStatus.BAD_REQUEST,
            "GOAL400_0",
            "커서값이 잘못되었습니다."),
    QUERY_INVALID(HttpStatus.BAD_REQUEST,
            "GOAL400_1",
            "query값이 잘못되었습니다."),
    NOT_FOUND_DELETE_GOALS(HttpStatus.NOT_FOUND,
            "GOAL404_2",
            "해당 팀에 삭제된 목표가 존재하지 않습니다."),
    NOT_A_DELETED(HttpStatus.BAD_REQUEST,
            "GOAL400_2",
            "복원할 목표가 없습니다.")
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
