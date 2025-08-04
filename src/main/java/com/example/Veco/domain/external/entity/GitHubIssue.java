package com.example.Veco.domain.external.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "github_issues")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GitHubIssue {

    @Id
    private Long githubIssueId; // GitHub의 실제 Issue ID

    @Column(nullable = false)
    private String nodeId;

    @Column(nullable = false)
    private Integer number; // Issue 번호

    @Column(nullable = false, length = 500)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String body;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssueState state; // OPEN, CLOSED

    @Column(nullable = false)
    private String repositoryFullName; // owner/repo 형태

    @Column(nullable = false)
    private String repositoryUrl;

    @Column(nullable = false)
    private String htmlUrl;

    @Column(nullable = false)
    private String creatorLogin;

    @Column(nullable = false)
    private Long creatorId;

    @Column
    private String creatorAvatarUrl;

    @Column
    private String assigneeLogin;

    @Column
    private Long assigneeId;

    @ElementCollection
    @CollectionTable(name = "github_issue_labels", joinColumns = @JoinColumn(name = "issue_id"))
    @Column(name = "label_name")
    private List<String> labels;

    @Column(nullable = false)
    private LocalDateTime githubCreatedAt;

    @Column(nullable = false)
    private LocalDateTime githubUpdatedAt;

    @Column
    private LocalDateTime githubClosedAt;

    @Column(nullable = false)
    private Integer commentsCount;

    @Column(nullable = false)
    private Boolean locked;

    @Column
    private String lockReason;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum IssueState {
        OPEN, CLOSED
    }
}
