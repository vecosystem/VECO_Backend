package com.example.Veco.domain.team.exception.code;

import com.example.Veco.global.apiPayload.ErrorReasonDTO;
import com.example.Veco.global.apiPayload.code.BaseErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
public enum TeamErrorCode implements BaseErrorStatus {

    _NOT_FOUND(HttpStatus.NOT_FOUND, "TEAM404_0", "해당 팀을 찾을 수 없습니다."),
    _TEAM_NOT_IN_WORKSPACE(HttpStatus.BAD_REQUEST, "TEAM404_1", "팀이 해당 워크스페이스에 없습니다."),
    _TEAM_COUNT_MISMATCH(HttpStatus.BAD_REQUEST, "TEAM404_2", "요청한 팀 개수와 워크스페이스의 팀 개수가 일치하지 않습니다.")
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
