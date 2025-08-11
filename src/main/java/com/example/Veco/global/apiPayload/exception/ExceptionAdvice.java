package com.example.Veco.global.apiPayload.exception;

import com.example.Veco.global.apiPayload.ApiResponse;
import com.example.Veco.global.apiPayload.code.BaseErrorStatus;
import com.example.Veco.global.apiPayload.code.ErrorStatus;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ExceptionAdvice {

    // @Valid 검증 실패 예외
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiResponse<Map<String, String>>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex
    ) {
        // 검사에 실패한 필드와 그에 대한 메시지를 저장하는 Map
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        // 커스텀 코드 넣고 응답 통일
        BaseErrorStatus code = ErrorStatus.VALID_FAILED;
        ApiResponse<Map<String, String>> errorResponse = ApiResponse.onFailure(
                code.getReasonHttpStatus().getCode(),
                code.getReasonHttpStatus().getMessage(),
                errors
        );

        // 에러 코드, 메시지와 함께 errors를 반환
        return ResponseEntity.status(code.getReasonHttpStatus().getHttpStatus()).body(errorResponse);
    }

    // 제약 조건 위반 예외: DB 제약조건 포함
    @ExceptionHandler(exception = ConstraintViolationException.class)
    public ResponseEntity<Object> validation(
            ConstraintViolationException e
    ) {

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

    // 요청 직렬화 실패
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException ex
    ){
        log.error("[ 요청 직렬화 실패 ]");

        // 직렬화 실패시, 응답 통일
        BaseErrorStatus code = ErrorStatus.BODY_TYPE_BAD_REQUEST;
        ApiResponse<Void> errorResponse = ApiResponse.onFailure(
                code.getReasonHttpStatus().getCode(),
                code.getReasonHttpStatus().getMessage(),
                null
        );

        return ResponseEntity.status(code.getReasonHttpStatus().getHttpStatus()).body(errorResponse);
    }

    // 쿼리 파라미터 검증
    @ExceptionHandler(HandlerMethodValidationException.class)
    protected ResponseEntity<ApiResponse<Map<String, String>>> handleHandlerMethodValidationException(
            HandlerMethodValidationException ex
    ) {

        log.error("[ 파라미터 검증 실패 ]");

        // 잘못된 쿼리와 위반 정보 저장
        Map<String, String> errors = new HashMap<>();
        ex.getParameterValidationResults().forEach(result ->
                errors.put(result.getMethodParameter().getParameterName(),
                        result.getResolvableErrors().getFirst().getDefaultMessage()));

        // 응답 통일
        BaseErrorStatus code = ErrorStatus.VALID_FAILED;
        ApiResponse<Map<String, String>> errorResponse = ApiResponse.onFailure(
                code.getReasonHttpStatus().getCode(),
                code.getReasonHttpStatus().getMessage(),
                errors
        );

        // 에러 코드, 메시지와 함께 errors를 반환
        return ResponseEntity.status(code.getReasonHttpStatus().getHttpStatus()).body(errorResponse);

    }

    // 요청 파라미터가 없을 때 발생하는 예외 처리
    @ExceptionHandler(MissingPathVariableException.class)
    protected ResponseEntity<ApiResponse<Map<String,String>>> handleMissingServletRequestParameterException(
            MissingPathVariableException ex
    ) {

        log.warn("[ MissingRequestParameterException ]: 필요한 파라미터가 요청에 없습니다.");

        // 존재하지 않는 파라미터 저장
        Map<String, String> errors = new HashMap<>();
        errors.put(ex.getVariableName(), "파라미터가 없습니다.");

        // 응답 통일
        BaseErrorStatus code = ErrorStatus.VALID_FAILED;
        ApiResponse<Map<String,String>> errorResponse = ApiResponse.onFailure(
                code.getReasonHttpStatus().getCode(),
                code.getReasonHttpStatus().getMessage(),
                errors
        );

        // 에러 코드, 메시지와 함께 errors를 반환
        return ResponseEntity.status(code.getReasonHttpStatus().getHttpStatus()).body(errorResponse);
    }

    // 요청 파라미터 타입 변환 실패했을 경우
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ApiResponse<Map<String,String>>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException ex
    ){
        log.warn("[ MethodArgumentTypeMismatchException ]: 파라미터 타입이 맞지 않습니다.");

        // 변환 실패한 파라미터 저장
        Map<String, String> errors = new HashMap<>();
        errors.put(ex.getName(), "파라미터 타입이 맞지 않습니다.");

        // 응답 통일
        BaseErrorStatus code = ErrorStatus.VALID_FAILED;
        ApiResponse<Map<String, String>> errorResponse = ApiResponse.onFailure(
                code.getReasonHttpStatus().getCode(),
                code.getReasonHttpStatus().getMessage(),
                errors
        );

        // 에러 코드, 메시지와 함께 errors를 반환
        return ResponseEntity.status(code.getReasonHttpStatus().getHttpStatus()).body(errorResponse);
    }

    // 커스텀 Exception 예외
    @ExceptionHandler(value = VecoException.class)
    public ResponseEntity<Object> onThrowException(
            VecoException e
    ) {

        // 정의한 커스텀 메시지 로그에 기록
        log.error("[ VecoException ]: {}", e.getErrorReasonHttpStatus().getMessage());

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
        log.error("[ {} ]: {}", e.getClass().getSimpleName(), e.getMessage());

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
