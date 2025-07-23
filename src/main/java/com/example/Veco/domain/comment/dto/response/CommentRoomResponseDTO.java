package com.example.Veco.domain.comment.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

public class CommentRoomResponseDTO {

    @Getter
    @AllArgsConstructor
    public static class AllCommentsResponseDTO {

        private Long commentRoomId;
        private int size;
        private List<CommentResponseDTO> comments;
    }
}
