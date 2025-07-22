package com.example.Veco.domain.comment.converter;

import com.example.Veco.domain.comment.dto.request.CommentCreateDTO;
import com.example.Veco.domain.comment.entity.Comment;
import com.example.Veco.domain.comment.entity.CommentRoom;
import com.example.Veco.domain.member.entity.Member;

public class CommentConverter {

    public static Comment toComment(CommentCreateDTO commentCreateDTO, CommentRoom commentRoom, Member member) {

        Comment comment = Comment.builder()
                .commentRoom(commentRoom)
                .member(member)
                .content(commentCreateDTO.getContent())
                .build();

        comment.setCommentRoom(commentRoom);
        comment.setMember(member);

        return comment;
    }
}
