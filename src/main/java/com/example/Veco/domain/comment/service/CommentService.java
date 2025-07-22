package com.example.Veco.domain.comment.service;

import com.example.Veco.domain.comment.dto.request.CommentCreateDTO;
import com.example.Veco.global.enums.Category;

public interface CommentService {
    Long addComment(CommentCreateDTO commentCreateDTO);
}
