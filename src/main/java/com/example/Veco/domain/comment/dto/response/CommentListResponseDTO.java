package com.example.Veco.domain.comment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentListResponseDTO {

    private int totalSize;
    private List<CommentResponseDTO> comments;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CommentResponseDTO{
        private Long commentId;
        private String content;
        private LocalDateTime createdAt;
        private AuthorResponseDTO author;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class AuthorResponseDTO {
        private Long authorId;
        private String authorName;
        private String profileImageUrl;
    }

}
