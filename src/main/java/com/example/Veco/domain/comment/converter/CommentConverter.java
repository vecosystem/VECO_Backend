package com.example.Veco.domain.comment.converter;

import com.example.Veco.domain.comment.dto.request.CommentCreateDTO;
import com.example.Veco.domain.comment.dto.response.CommentListResponseDTO;
import com.example.Veco.domain.comment.entity.Comment;
import com.example.Veco.domain.comment.entity.CommentRoom;
import com.example.Veco.domain.member.entity.Member;

import java.util.ArrayList;
import java.util.List;

public class CommentConverter {

    public static Comment toComment(CommentCreateDTO commentCreateDTO, CommentRoom commentRoom, Member member) {
        Comment comment = Comment.builder()
                .content(commentCreateDTO.getContent())
                .build();

        comment.setCommentRoom(commentRoom);
        comment.setMember(member);

        return comment;
    }

    public static CommentListResponseDTO toCommentResponseDTO(List<Comment> comments) {

        List<CommentListResponseDTO.CommentResponseDTO> commentResponseDTOS = new ArrayList<>();

        comments.forEach(comment -> {
            CommentListResponseDTO.AuthorResponseDTO authorDTO = CommentListResponseDTO.AuthorResponseDTO.builder()
                    .authorId(comment.getMember().getId())
                    .authorName(comment.getMember().getName())
                    .profileImageUrl(comment.getMember().getProfile().getProfileImageUrl())
                    .build();

            CommentListResponseDTO.CommentResponseDTO commentDTO = CommentListResponseDTO.CommentResponseDTO.builder()
                    .commentId(comment.getId())
                    .content(comment.getContent())
                    .author(authorDTO)
                    .createdAt(comment.getCreatedAt())
                    .build();

            commentResponseDTOS.add(commentDTO);
        });

        return CommentListResponseDTO.builder()
                .comments(commentResponseDTOS)
                .totalSize(comments.size())
                .build();
    }
}
