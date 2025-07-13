package com.example.Veco.domain.team.entity;


import com.example.Veco.global.apiPayload.ErrorReasonDTO;
import com.example.Veco.global.apiPayload.code.BaseErrorStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum TeamErrorCode implements BaseErrorStatus {

    NOT_FOUND(HttpStatus.NOT_FOUND,
            "TEAM404_0",
            "해당 팀이 존재하지 않습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN,
            "TEAM403_0",
            "담당자 중 같은 팀원이 아닌 사용자가 있습니다.")
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
