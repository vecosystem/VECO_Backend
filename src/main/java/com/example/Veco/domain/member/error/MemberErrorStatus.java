package com.example.Veco.domain.member.error;

import com.example.Veco.global.apiPayload.ErrorReasonDTO;
import com.example.Veco.global.apiPayload.code.BaseErrorStatus;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum MemberErrorStatus implements BaseErrorStatus {
    _MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "MEMBER4001", "해당 멤버가 없습니다."),
    _PROFILE_NOT_FOUND(HttpStatus.NOT_FOUND, "PROFILE4001", "프로필 정보가 존재하지 않습니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "MEMBER403_0", "접근이 금지되었습니다."),
    _PROFILE_IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "PROFILE4002", "프로필 이미지가 존재하지 않습니다."),
    _MEMBER_NOT_IN_WORKSPACE(HttpStatus.NOT_FOUND, "MEMBER4002", "멤버가 해당 워크스페이스에 없습니다."),
    _INVALID_MEMBER_INCLUDE(HttpStatus.NOT_FOUND, "MEMBER4003", "존재하지 않는 멤버가 포함되어 있습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .isSuccess(false)
                .code(code)
                .message(message)
                .httpStatus(httpStatus)
                .build();
    }
}
