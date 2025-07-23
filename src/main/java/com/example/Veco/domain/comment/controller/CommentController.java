package com.example.Veco.domain.comment.controller;

import com.example.Veco.domain.comment.dto.request.CommentCreateDTO;
import com.example.Veco.domain.comment.dto.response.CommentResponseDTO;
import com.example.Veco.domain.comment.service.CommentService;
import com.example.Veco.global.apiPayload.ApiResponse;
import com.example.Veco.global.enums.Category;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ApiResponse<Long> createComment(@Valid @RequestBody CommentCreateDTO commentCreateDTO) {
        return ApiResponse.onSuccess(commentService.addComment(commentCreateDTO));
    }

    @GetMapping
    public ApiResponse<List<CommentResponseDTO>> getAllComments(@RequestParam("targetId") Long targetId,
                                                                @RequestParam("category") Category category) {
        return ApiResponse.onSuccess(commentService.getComments(targetId, category));
    }
}
