package com.example.Veco.domain.comment.service;

import com.example.Veco.domain.comment.dto.response.CommentResponseDTO;
import com.example.Veco.domain.comment.entity.Comment;
import com.example.Veco.domain.comment.entity.CommentRoom;
import com.example.Veco.domain.comment.repository.repository.CommentRepository;
import com.example.Veco.domain.mapping.repository.CommentRoomRepository;
import com.example.Veco.global.enums.Category;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentRoomRepository commentRoomRepository;

    @BeforeEach
    void setUp() {
        CommentRoom commentRoom = CommentRoom.builder()
                .roomType(Category.GOAL)
                .targetId(1L)
                .build();

        Comment comment1 = Comment.builder()
                .content("content1")
                .build();

        Comment comment2 = Comment.builder()
                .content("comment2")
                .build();

        Comment comment3 = Comment.builder()
                .content("comment3")
                .build();

        Comment comment4 = Comment.builder()
                .content("comment4")
                .build();

        Comment comment5 = Comment.builder()
                .content("comment5")
                .build();

        comment1.setCommentRoom(commentRoom);
        comment2.setCommentRoom(commentRoom);
        comment3.setCommentRoom(commentRoom);
        comment4.setCommentRoom(commentRoom);
        comment5.setCommentRoom(commentRoom);

        commentRoomRepository.save(commentRoom);
    }


    @DisplayName("")
    @Test
    void getComments(){

        //given when
        List<Comment> comments = commentRepository
                .findAllByCommentRoomOrderByIdDesc(commentRoomRepository.findById(1L).get()).get();


        //then
        Assertions.assertThat(comments).isNotNull();
        Assertions.assertThat(comments.size()).isEqualTo(5);
        Assertions.assertThat(comments.get(0).getContent()).isEqualTo("comment5");
    }
}