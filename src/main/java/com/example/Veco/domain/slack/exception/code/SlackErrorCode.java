package com.example.Veco.domain.slack.exception.code;

import com.example.Veco.global.apiPayload.ErrorReasonDTO;
import com.example.Veco.global.apiPayload.code.BaseErrorStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum SlackErrorCode implements BaseErrorStatus {

    TOKEN_FAILED(HttpStatus.BAD_REQUEST,
            "SLACK400_0",
            "Bot 토큰 발급에 실패했습니다."),
    LIST_FAILED(HttpStatus.BAD_REQUEST,
            "SLACK400_1",
            "Slack 채널 조회에 실패했습니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return null;
    }
}
