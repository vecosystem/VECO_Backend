package com.example.Veco.domain.goal.exception.code;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@Getter
public enum GoalSuccessCode{

    OK(HttpStatus.OK,
            "GOAL200",
            "성공적으로 처리했습니다."),
    CREATE(HttpStatus.CREATED,
            "GOAL201_0",
            "성공적으로 생성했습니다."),
    IMAGE_UPLOAD(HttpStatus.CREATED,
            "GOAL201_1",
            "이미지를 성공적으로 저장했습니다."),
    NO_CONTENT(HttpStatus.NO_CONTENT,
            "GOAL204_0",
            "반영할 콘텐츠가 없습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
