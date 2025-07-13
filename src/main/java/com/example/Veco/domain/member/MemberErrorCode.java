package com.example.Veco.domain.member;

import com.example.Veco.global.apiPayload.ErrorReasonDTO;
import com.example.Veco.global.apiPayload.code.BaseErrorStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum MemberErrorCode implements BaseErrorStatus {

    NOT_FOUND(HttpStatus.NOT_FOUND,
            "MEMBER404_0",
            "해당 사용자를 찾지 못했습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN,
            "MEMBER403_0",
            "권한이 없습니다.")
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
