package com.example.Veco.domain.goal.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum GoalErrorCode {

    FORBIDDEN(HttpStatus.FORBIDDEN,
            "GOAL403_0",
            "권한이 없습니다."),
    NOT_FOUND_IN_TEAM(HttpStatus.NOT_FOUND,
            "GOAL404_0",
            "해당 팀에 목표가 존재하지 않습니다."),
    NOT_FOUND(HttpStatus.NOT_FOUND,
            "GOAL404_1",
            "해당 목표가 존재하지 않습니다.")
    ;

    // 응답 통일 시 변경
    GoalErrorCode(HttpStatus httpStatus, String code, String message) {
    }
}
