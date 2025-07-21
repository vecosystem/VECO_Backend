package com.example.Veco.domain.comment.repository.repository;

import com.example.Veco.domain.comment.entity.CommentRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRoomRepository extends JpaRepository<CommentRoom, Long> {
}
