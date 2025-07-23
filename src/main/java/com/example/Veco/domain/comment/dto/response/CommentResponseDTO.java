package com.example.Veco.domain.comment.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentResponseDTO {

    private Long commentId;
    private String content;
    private LocalDateTime createdAt;
    private AuthorResponseDTO author;

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
