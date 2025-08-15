package com.example.Veco.domain.comment.dto.response;

import com.example.Veco.domain.external.dto.response.ExternalResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class CommentResponseDTO {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CommentListDTO {
        private Integer cnt;
        List<CommentResponseDTO.CommentInfoDTO> info;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CommentInfoDTO {
        private Long id;
        private String profileUrl;
        private String name;
        private LocalDateTime createdAt;
        private String content;
    }
}
