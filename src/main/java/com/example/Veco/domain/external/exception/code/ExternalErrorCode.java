package com.example.Veco.domain.external.exception.code;

import com.example.Veco.global.apiPayload.ErrorReasonDTO;
import com.example.Veco.global.apiPayload.code.BaseErrorStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ExternalErrorCode implements BaseErrorStatus {
    NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 외부이슈가 존재하지 않습니다.", "EXTERNAL404"),
    DEADLINE_INVALID(HttpStatus.BAD_REQUEST, "올바르지 않은 날짜 형식입니다.", "EXTERNAL400"),
    NOT_FOUND_DELETE_EXTERNALS(HttpStatus.NOT_FOUND, "삭제된 외부이슈들을 발견하지 못했습니다.", "EXTERNAL404"),
    NOT_A_DELETE(HttpStatus.NOT_FOUND, "복원할 외부이슈가 없습니다.", "EXTERNAL404"),
    NOT_SAME_TEAM(HttpStatus.BAD_REQUEST, "동일한 팀의 요청이 아닙니다.", "EXTERNAL400"),
    ;

    private HttpStatus httpStatus;
    private String message;
    private String code;

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .httpStatus(httpStatus)
                .message(message)
                .code(code)
                .build();
    }
}
