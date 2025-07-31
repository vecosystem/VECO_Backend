package com.example.Veco.domain.memberNotification.exception.code;

import com.example.Veco.global.apiPayload.code.BaseSuccessStatus;
import com.example.Veco.global.apiPayload.dto.SuccessReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum MemberNotiSuccessCode implements BaseSuccessStatus {

    UPDATE(HttpStatus.OK,
            "MEMBER_NOTIFICATION200_1",
            "성공적으로 수정했습니다."),
    DELETE(HttpStatus.OK,
            "MEMBER_NOTIFICATION200_2",
            "성공적으로 삭제했습니다.");

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
