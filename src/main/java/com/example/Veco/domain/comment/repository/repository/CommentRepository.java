package com.example.Veco.domain.comment.repository.repository;

import com.example.Veco.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
