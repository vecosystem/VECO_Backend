package com.example.Veco.domain.github.exception.code;

import com.example.Veco.global.apiPayload.ErrorReasonDTO;
import com.example.Veco.global.apiPayload.code.BaseErrorStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum GitHubErrorCode implements BaseErrorStatus {


    INSTALLATION_INFO_NOT_FOUND(HttpStatus.NOT_FOUND, "해당하는 연동정보가 존재하지 않습니다.", "GITHUB400"),
    ;

    private HttpStatus httpStatus;
    private String message;
    private String code;


    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return null;
    }
}
