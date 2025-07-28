package com.example.Veco.domain.issue.exception.code;

import com.example.Veco.global.apiPayload.code.BaseSuccessStatus;
import com.example.Veco.global.apiPayload.dto.SuccessReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum IssueSuccessCode implements BaseSuccessStatus {

    OK(HttpStatus.OK,
            "ISSUE200_0",
            "성공적으로 처리했습니다.");

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
