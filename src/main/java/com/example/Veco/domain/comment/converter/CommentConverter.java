package com.example.Veco.domain.comment.converter;

import com.example.Veco.domain.comment.dto.request.CommentCreateDTO;
import com.example.Veco.domain.comment.dto.response.CommentListResponseDTO;
import com.example.Veco.domain.comment.dto.response.CommentResponseDTO;
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

    public static CommentResponseDTO.CommentListDTO toCommentListDTO(List<Comment> comments) {

        List<CommentResponseDTO.CommentInfoDTO> commentInfoDTOS = new ArrayList<>();

        comments.forEach(comment -> {

            CommentResponseDTO.CommentInfoDTO commentDTO = CommentResponseDTO.CommentInfoDTO.builder()
                    .id(comment.getId())
                    .content(comment.getContent())
                    .createdAt(comment.getCreatedAt())
                    .name(comment.getMember().getName())
                    .profileUrl(comment.getMember().getProfile().getProfileImageUrl())
                    .build();

            commentInfoDTOS.add(commentDTO);
        });

        return CommentResponseDTO.CommentListDTO.builder()
                .cnt(commentInfoDTOS.size())
                .info(commentInfoDTOS)
                .build();
    }
}
