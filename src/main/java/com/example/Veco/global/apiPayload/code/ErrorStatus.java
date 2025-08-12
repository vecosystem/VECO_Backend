package com.example.Veco.global.apiPayload.code;

import com.example.Veco.global.apiPayload.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorStatus{
    // 가장 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),

    EXTERNAL_NOT_FOUND(HttpStatus.NOT_FOUND, "EXTERNAL400", "해당하는 외부 이슈가 존재하지 않습니다."),
    GOAL_NOT_FOUND(HttpStatus.NOT_FOUND, "GOAL400", "해당하는 목표가 존재하지 않습니다."),

    VALID_FAILED(HttpStatus.BAD_REQUEST,
            "VALID400_1",
            "잘못된 파라미터입니다."),
    BODY_TYPE_BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400_1", "요청한 Body 타입이 잘못되었습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build()
                ;
    }
}
