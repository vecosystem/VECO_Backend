package com.example.Veco.domain.issue.exception.code;

import com.example.Veco.global.apiPayload.code.BaseSuccessStatus;
import com.example.Veco.global.apiPayload.dto.SuccessReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum IssueSuccessCode implements BaseSuccessStatus {

    OK(HttpStatus.OK,
            "ISSUE200_0",
            "성공적으로 처리했습니다."),
    DELETE(HttpStatus.OK,
            "ISSUE200_2",
            "성공적으로 삭제했습니다."),
    UPDATE(HttpStatus.OK,
            "ISSUE200_1",
            "성공적으로 수정했습니다."),
    NO_CONTENT(HttpStatus.NO_CONTENT,
            "ISSUE204_0",
            "반영할 콘텐츠가 없습니다.");

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
