package com.example.Veco.domain.comment.service;

import com.example.Veco.domain.comment.dto.request.CommentCreateDTO;
import com.example.Veco.domain.comment.dto.response.CommentResponseDTO;
import com.example.Veco.global.enums.Category;

import java.util.List;

public interface CommentService {
    Long addComment(CommentCreateDTO commentCreateDTO);
    List<CommentResponseDTO> getComments(Long targetId, Category category);
}
