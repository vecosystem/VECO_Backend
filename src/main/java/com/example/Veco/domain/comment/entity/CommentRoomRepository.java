package com.example.Veco.domain.comment.entity;

import com.example.Veco.global.enums.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRoomRepository extends JpaRepository<CommentRoom, Long> {
    CommentRoom findByRoomTypeAndTargetId(Category category, Long goalId);
}
