package com.example.Veco.domain.github.dto.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

public class IssueCommentWebhookDTO {

    private String action;
    private Comment comment;

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Comment {
        private Long id;             // 댓글 ID
        private String nodeId;       // GraphQL Node ID
        private String body;         // 댓글 내용
        private String createdAt;    // 작성 시간
        private String updatedAt;    // 수정 시간
        private String htmlUrl;      // 댓글 URL
        private String authorAssociation; // "OWNER", "MEMBER", "CONTRIBUTOR", "NONE" 등
    }


}
