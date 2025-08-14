package com.example.Veco.domain.github.dto.request;

import lombok.Builder;
import lombok.Data;

public class GitHubApiRequestDTO {

    @Data
    @Builder
    public static class IssueCreateRequestDTO {
        private String title;
        private String body;
        private String[] assignees;
        private String[] labels;
        private Integer milestone;
    }
}
