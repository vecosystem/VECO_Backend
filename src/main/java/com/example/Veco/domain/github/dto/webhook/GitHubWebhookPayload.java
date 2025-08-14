package com.example.Veco.domain.github.dto.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubWebhookPayload {

    private String action;
    private Issue issue;
    private Repository repository;
    private User sender;
    private Installation installation;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Issue {
        private Long id;

        @JsonProperty("node_id")
        private String nodeId;

        private String url;

        @JsonProperty("html_url")
        private String htmlUrl;

        @JsonProperty("comments_url")
        private String commentsUrl;

        private Integer number;
        private String state;
        private String title;
        private String body;
        private User user;
        private List<Label> labels;
        private User assignee;
        private List<User> assignees;
        private Milestone milestone;
        private Boolean locked;

        @JsonProperty("active_lock_reason")
        private String activeLockReason;

        private Integer comments;

        @JsonProperty("closed_at")
        private String closedAt;

        @JsonProperty("created_at")
        private String createdAt;

        @JsonProperty("updated_at")
        private String updatedAt;

        @JsonProperty("author_association")
        private String authorAssociation;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Repository {
        private Long id;

        @JsonProperty("node_id")
        private String nodeId;

        private String name;

        @JsonProperty("full_name")
        private String fullName;

        private User owner;

        @JsonProperty("private")
        private Boolean isPrivate;

        @JsonProperty("html_url")
        private String htmlUrl;

        private String description;
        private Boolean fork;
        private String url;

        @JsonProperty("created_at")
        private String createdAt;

        @JsonProperty("updated_at")
        private String updatedAt;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class User {
        private Long id;

        @JsonProperty("node_id")
        private String nodeId;

        private String login;

        @JsonProperty("avatar_url")
        private String avatarUrl;

        @JsonProperty("gravatar_id")
        private String gravatarId;

        private String url;

        @JsonProperty("html_url")
        private String htmlUrl;

        private String type;

        @JsonProperty("site_admin")
        private Boolean siteAdmin;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Label {
        private Long id;

        @JsonProperty("node_id")
        private String nodeId;

        private String url;
        private String name;
        private String description;
        private String color;

        @JsonProperty("default")
        private Boolean isDefault;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Milestone {
        private Long id;

        @JsonProperty("node_id")
        private String nodeId;

        private String url;

        @JsonProperty("html_url")
        private String htmlUrl;

        private Integer number;
        private String state;
        private String title;
        private String description;
        private User creator;

        @JsonProperty("open_issues")
        private Integer openIssues;

        @JsonProperty("closed_issues")
        private Integer closedIssues;

        @JsonProperty("created_at")
        private String createdAt;

        @JsonProperty("updated_at")
        private String updatedAt;

        @JsonProperty("closed_at")
        private String closedAt;

        @JsonProperty("due_on")
        private String dueOn;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Installation {
        @JsonProperty("id")
        private Long id;

        @JsonProperty("node_id")
        private String nodeId;
    }

}
