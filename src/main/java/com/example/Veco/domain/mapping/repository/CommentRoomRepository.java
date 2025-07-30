package com.example.Veco.domain.mapping.repository;

import com.example.Veco.domain.comment.entity.CommentRoom;
import com.example.Veco.global.enums.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentRoomRepository extends JpaRepository<CommentRoom, Long> {
    CommentRoom findByRoomTypeAndTargetId(Category category, Long targetId);
    boolean existsByRoomTypeAndTargetId(Category category, Long targetId);
}
