package com.example.Veco.global.apiPayload;

import com.example.Veco.global.apiPayload.code.BaseSuccessStatus;
import com.example.Veco.global.apiPayload.code.SuccessStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
@Schema(description = "API 응답 래퍼")
public class ApiResponse<T> {

    @JsonProperty("isSuccess")
    @Schema(description = "성공 여부", example = "true")
    private boolean success;

    @Schema(description = "응답 코드", example = "SUCCESS")
    private String code;

    @Schema(description = "응답 메시지", example = "요청이 성공했습니다.")
    private String message;

    @Schema(description = "응답 데이터")
    private T result;

    // 커스텀 성공 응답
    public static <T> ApiResponse<T> onSuccess(BaseSuccessStatus status, T data) {
        return new ApiResponse<>(
                true,
                status.getReasonHttpStatus().getCode(),
                status.getReasonHttpStatus().getMessage(),
                data
        );
    }

    public static <T> ApiResponse<T> onSuccess(T result) {
        return new ApiResponse<>(true, SuccessStatus._OK.getCode(), SuccessStatus._OK.getMessage(), result);
    }

    public static <T> ApiResponse<T> onSuccessWithNullResult() {
        return new ApiResponse<>(true, SuccessStatus._OK.getCode(), SuccessStatus._OK.getMessage(), null);
    }

    public static <T> ApiResponse<T> onFailure(String code, String message, T data) {
        return new ApiResponse<>(false, code, message, data);
    }
}
