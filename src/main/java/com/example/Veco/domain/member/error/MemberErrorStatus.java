package com.example.Veco.domain.member.error;

import com.example.Veco.global.apiPayload.ErrorReasonDTO;
import com.example.Veco.global.apiPayload.code.BaseErrorStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorStatus implements BaseErrorStatus {
    _MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER4001", "해당 멤버가 없습니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "MEMBER403_0", "접근이 금지되었습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .isSuccess(false)
                .code(code)
                .message(message)
                .httpStatus(httpStatus)
                .build();
    }
}
