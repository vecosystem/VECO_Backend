package com.example.Veco.domain.github.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

public class GitHubApiResponseDTO {

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GitHubRepositoryResponseDTO {
        @JsonProperty("total_count")
        private Integer totalCount;

        private List<GitHubRepositoryDTO> repositories;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GitHubRepositoryDTO {

        private Long id;
        private String name;           // "your-repository"
        private String fullName;       // "your-username/your-repository"

        @JsonProperty("html_url")
        private String htmlUrl;

        private GitHubOwner owner;

        @JsonProperty("default_branch")
        private String defaultBranch;

        private String description;
        private boolean isPrivate;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class GitHubOwner {
            private String login;      // "your-username"
            private String type;       // "User" or "Organization"

            @JsonProperty("avatar_url")
            private String avatarUrl;
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GitHubIssueResponseDTO {
        private Long id;
        private Integer number;
        private String title;
        private String body;
        private String state;

        @JsonProperty("html_url")
        private String htmlUrl;

        @JsonProperty("created_at")
        private String createdAt;

        private GitHubUser user;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class GitHubUser {
            private String login;
            private String type;

            @JsonProperty("avatar_url")
            private String avatarUrl;
        }
    }
}
