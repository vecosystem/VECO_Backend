package com.example.Veco.domain.notification.exception.code;

import com.example.Veco.global.apiPayload.code.BaseSuccessStatus;
import com.example.Veco.global.apiPayload.dto.SuccessReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum NotiSuccessCode implements BaseSuccessStatus {

    OK(HttpStatus.OK,
            "NOTIFICATION200",
            "성공적으로 처리했습니다."),
    NO_CONTENT(HttpStatus.NO_CONTENT,
            "NOTIFICATION204_0",
            "알림이 없습니다."),;

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
