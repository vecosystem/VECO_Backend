package com.example.Veco.global.apiPayload.exception;

import com.example.Veco.global.apiPayload.ApiResponse;
import com.example.Veco.global.apiPayload.code.BaseErrorStatus;
import com.example.Veco.global.apiPayload.code.ErrorStatus;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ExceptionAdvice {

    // 제약 조건 위반 예외
    @ExceptionHandler(exception = ConstraintViolationException.class)
    public ResponseEntity<Object> validation(ConstraintViolationException e) {

        log.error("[ConstraintViolationException] 제약 조건 위반");
        // 제약 조건 위반 정보를 저장할 Map
        Map<String, String> errors = new HashMap<>();

        e.getConstraintViolations().forEach(violation -> {
            String propertyPath = violation.getPropertyPath().toString();
            // 마지막 필드명만 추출 (예: user.name -> name)
            String fieldName = propertyPath.contains(".") ?
                    propertyPath.substring(propertyPath.lastIndexOf(".") + 1) : propertyPath;

            // 필드명(쿼리명) : 위반 사항
            errors.put(fieldName, violation.getMessage());
        });

        // 응답 통일: result에 위반 사항 담아 전달
        BaseErrorStatus constraintErrorCode = ErrorStatus._BAD_REQUEST;
        ApiResponse<Map<String, String>> errorResponse = ApiResponse.onFailure(
                constraintErrorCode.getReasonHttpStatus().getCode(),
                constraintErrorCode.getReasonHttpStatus().getMessage(),
                errors
        );

        // 에러 코드, 메시지와 함께 errors를 반환
        return ResponseEntity.status(constraintErrorCode.getReasonHttpStatus().getHttpStatus()).body(errorResponse);
    }

    // 커스텀 Exception 예외
    @ExceptionHandler(value = VecoException.class)
    public ResponseEntity<Object> onThrowException(VecoException e) {

        // 정의한 커스텀 메시지 로그에 기록
        log.error("[커스텀 Exception]: {}", e.getErrorReasonHttpStatus().getMessage());

        // 에러 메시지 반환
        Map<String, String> result = new HashMap<>();
        result.put(e.getClass().getSimpleName(), e.getMessage());

        HttpStatus status = e.getErrorReasonHttpStatus().getHttpStatus();
        return ResponseEntity.status(status).body(
                ApiResponse.onFailure(
                        e.getErrorReasonHttpStatus().getCode(),
                        e.getErrorReasonHttpStatus().getMessage(),
                        result
                )
        );
    }

    // 그밖에 모든 Exception들
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<Object> exception(Exception e) {

        // 정의하지 않은 예외는 로그로 확인
        log.error("{}:{}", e.getClass().getSimpleName(), e.getMessage());

        // 에러 메시지 반환
        Map<String, String> result = new HashMap<>();
        result.put(e.getClass().getSimpleName(), e.getMessage());

        // 응답은 500으로 반환: 결과에 로그 반환
        BaseErrorStatus status = ErrorStatus._INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status.getReasonHttpStatus().getHttpStatus()).body(
                ApiResponse.onFailure(
                        status.getReasonHttpStatus().getCode(),
                        status.getReasonHttpStatus().getMessage(),
                        result
                )
        );
    }

}
