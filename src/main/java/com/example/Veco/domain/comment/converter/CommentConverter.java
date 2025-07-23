package com.example.Veco.domain.comment.converter;

import com.example.Veco.domain.comment.dto.request.CommentCreateDTO;
import com.example.Veco.domain.comment.dto.response.CommentResponseDTO;
import com.example.Veco.domain.comment.entity.Comment;
import com.example.Veco.domain.comment.entity.CommentRoom;
import com.example.Veco.domain.member.entity.Member;

public class CommentConverter {

    public static Comment toComment(CommentCreateDTO commentCreateDTO, CommentRoom commentRoom, Member member) {
        Comment comment = Comment.builder()
                .content(commentCreateDTO.getContent())
                .build();

        comment.setCommentRoom(commentRoom);
        comment.setMember(member);

        return comment;
    }

    public static CommentResponseDTO toCommentResponseDTO(Comment comment) {
        String profileImageUrl = null;
        if (comment.getMember().getProfile() != null) {
            profileImageUrl = comment.getMember().getProfile().getProfileImageUrl();
        }
        
        CommentResponseDTO.AuthorResponseDTO authorDTO = CommentResponseDTO.AuthorResponseDTO.builder()
                .authorId(comment.getMember().getId())
                .authorName(comment.getMember().getName())
                .profileImageUrl(profileImageUrl)
                .build();

        return CommentResponseDTO.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .author(authorDTO)
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
