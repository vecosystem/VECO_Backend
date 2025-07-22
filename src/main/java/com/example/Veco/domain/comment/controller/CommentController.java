package com.example.Veco.domain.comment.controller;

import com.example.Veco.domain.comment.dto.request.CommentCreateDTO;
import com.example.Veco.domain.comment.service.CommentService;
import com.example.Veco.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ApiResponse<Long> createComment(@RequestBody CommentCreateDTO commentCreateDTO) {

        log.info("Category value: {}", commentCreateDTO.getCategory());


        return ApiResponse.onSuccess(commentService.addComment(commentCreateDTO));
    }
}
