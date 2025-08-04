package com.example.Veco.domain.slack.exception.code;

import com.example.Veco.global.apiPayload.code.BaseSuccessStatus;
import com.example.Veco.global.apiPayload.dto.SuccessReasonDTO;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SlackSuccessCode implements BaseSuccessStatus {

    CONNECTING(HttpStatus.OK,
            "SLACK200_1",
            "성공적으로 연동했습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
    @Override
    public SuccessReasonDTO getReasonHttpStatus() {
        return SuccessReasonDTO.builder()
                .isSuccess(true)
                .httpStatus(httpStatus)
                .code(code)
                .message(message)
                .build();
    }
}
