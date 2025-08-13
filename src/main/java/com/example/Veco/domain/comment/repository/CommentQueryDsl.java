package com.example.Veco.domain.comment.repository;

import com.example.Veco.domain.comment.entity.Comment;
import com.example.Veco.domain.comment.entity.CommentRoom;

import java.util.List;

public interface CommentQueryDsl {
    List<Comment> findByCommentRoomOrderByIdDesc(CommentRoom commentRoom);
}
