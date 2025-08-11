package com.example.Veco.domain.comment.repository;

import com.example.Veco.domain.comment.entity.Comment;
import com.example.Veco.domain.comment.entity.CommentRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByCommentRoomOrderByIdDesc(CommentRoom commentRoom);
    List<Comment> findAllByCommentRoomOrderByIdAsc(CommentRoom commentRoom);
}
