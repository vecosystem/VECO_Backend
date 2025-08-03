package com.example.Veco.domain.memberNotification.exception.code;

import com.example.Veco.global.apiPayload.ErrorReasonDTO;
import com.example.Veco.global.apiPayload.code.BaseErrorStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum MemberNotiErrorCode implements BaseErrorStatus {

    NOT_FOUND(HttpStatus.NOT_FOUND,
            "MEMBER_NOTIFICATION404_0",
                    "해당 알림을 찾을 수 없습니다."),
    ID_LIST_INVALID(HttpStatus.NOT_FOUND,
            "MEMBER_NOTIFICATION404_1",
            "일부 알림을 찾을 수 없습니다."),
    QUERY_INVALID(HttpStatus.BAD_REQUEST,
            "MEMBER_NOTIFICATION400_0",
            "Query값이 잘못되었습니다.");

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
