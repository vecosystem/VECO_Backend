package com.example.Veco.domain.comment.repository;

import com.example.Veco.domain.comment.entity.Comment;
import com.example.Veco.domain.comment.entity.CommentRoom;
import com.example.Veco.domain.external.entity.External;
import com.example.Veco.domain.external.repository.ExternalRepository;
import com.example.Veco.domain.mapping.repository.CommentRoomRepository;
import com.example.Veco.global.enums.Category;
import com.example.Veco.global.enums.Priority;
import com.example.Veco.global.enums.State;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CommentRepositoryTest {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentRoomRepository commentRoomRepository;

    @Autowired
    private ExternalRepository externalRepository;

    CommentRoom commentRoom;

    Comment comment1, comment2, comment3;

    @BeforeEach
    void setUp() {

        External external = External.builder()
                .title("title")
                .description("description")
                .name("VECO-e1")
                .state(State.NONE)
                .priority(Priority.NONE)
                .build();

        externalRepository.save(external);

         commentRoom = CommentRoom.builder()
                .targetId(external.getId())
                .roomType(Category.EXTERNAL)
                .build();

        commentRoomRepository.save(commentRoom);

        comment1 = Comment.builder()
                .commentRoom(commentRoom)
                .content("content1")
                .build();

        comment2 = Comment.builder()
                .commentRoom(commentRoom)
                .content("content2")
                .build();

        comment3 = Comment.builder()
                .commentRoom(commentRoom)
                .content("content3")
                .build();

        commentRepository.saveAll(List.of(comment1, comment2, comment3));
    }


    @DisplayName("")
    @Test
    void findAllByCommentRoomOrderByIdAcs(){

        //given
        List<Comment> comments = commentRepository.findAllByCommentRoomOrderByIdAsc(commentRoom);

        //when

        //then
        assertThat(comments).hasSize(3);
        assertThat(comments.get(0).getId()).isEqualTo(comment1.getId());
    }
}