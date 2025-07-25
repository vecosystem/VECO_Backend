package com.example.Veco.domain.comment.controller;

import com.example.Veco.domain.comment.dto.request.CommentCreateDTO;
import com.example.Veco.domain.comment.dto.response.CommentResponseDTO;
import com.example.Veco.domain.comment.service.CommentService;
import com.example.Veco.global.apiPayload.ApiResponse;
import com.example.Veco.global.enums.Category;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
@Tag(name = "댓글 API")
public class CommentController {

    private final CommentService commentService;

    @Operation(
            summary = "댓글 작성 API",
            description = "특정 리소스(이슈, 목표, 외부 이슈)에 댓글을 작성합니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "댓글 작성 성공",
                    content = @Content(schema = @Schema(implementation = Long.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 데이터",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "대상 리소스를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PostMapping
    public ApiResponse<Long> createComment(
            @Parameter(description = "댓글 작성 요청 정보", required = true)
            @Valid @RequestBody CommentCreateDTO commentCreateDTO) {
        return ApiResponse.onSuccess(commentService.addComment(commentCreateDTO));
    }

    @Operation(
            summary = "댓글 목록 조회 API",
            description = "특정 리소스(이슈, 목표, 외부 이슈)의 댓글 목록을 조회합니다. 최신순으로 정렬되어 반환됩니다."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "댓글 목록 조회 성공",
                    content = @Content(schema = @Schema(implementation = CommentResponseDTO.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "대상 리소스를 찾을 수 없음",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @GetMapping
    public ApiResponse<List<CommentResponseDTO>> getAllComments(
            @Parameter(description = "댓글을 조회할 대상 리소스의 ID", required = true, example = "1")
            @RequestParam("targetId") Long targetId,
            @Parameter(description = "리소스 카테고리 (ISSUE, GOAL, EXTERNAL)", required = true, example = "ISSUE")
            @RequestParam("category") Category category) {
        return ApiResponse.onSuccess(commentService.getComments(targetId, category));
    }

}
