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
    MESSAGE_POST_FAILED(HttpStatus.BAD_REQUEST,
            "SLACK400_2",
            "Slack 메시지 전송에 실패했습니다."),
    JOIN_FAILED(HttpStatus.BAD_REQUEST,
            "SLACK400_3",
            "기본 채널 참여에 실패했습니다."),
    NOT_LINKED(HttpStatus.BAD_REQUEST,
            "SLACK400_4",
            "해당 워크스페이스가 Slack과 연동되어 있지 않습니다."),
    REINSTALL(HttpStatus.UNAUTHORIZED,
            "SLACK401_0",
            "Slack Access Token이 만료되었습니다. App을 재설치해야 합니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .isSuccess(false)
                .httpStatus(status)
                .code(code)
                .message(message)
                .build();
    }
}
