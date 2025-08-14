package com.example.Veco.domain.github.dto.webhook;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Data
public class GitHubPullRequestPayload {
    private String action;      // PR 액션 타입
    private Long number;
    @JsonProperty("pull_request")
    private PullRequest pullRequest; // PR 상세 정보
    private Repository repository;   // 저장소 정보
    private Installation installation; // GitHub App 설치 정보

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Getter
    @Data
    public static class PullRequest {
        private Long id;           // PR 고유 ID
        private String nodeId;     // GraphQL Node ID
        private Long number;       // PR 번호 (#123)
        private String title;      // PR 제목
        private String body;       // PR 설명
        private String state;      // "open", "closed"
        private Boolean locked;    // 잠김 상태
        private Boolean draft;     // 드래프트 여부
        private Boolean merged;    // 머지 여부
        private Boolean mergeable; // 머지 가능 여부
        private String mergeableState; // "clean", "dirty", "unstable", "blocked"
        private User user;         // PR 생성자
        private User assignee;     // 담당자
        private List<User> assignees; // 담당자 목록
        private List<User> requestedReviewers; // 리뷰 요청된 사용자들
        private String createdAt;   // 생성 시간
        private String updatedAt;   // 수정 시간
        private String closedAt;    // 닫힌 시간
        private String mergedAt;    // 머지 시간
        private String mergeCommitSha; // 머지 커밋 SHA

        // Head와 Base 브랜치 정보
        private Branch head;        // 소스 브랜치
        private Branch base;        // 대상 브랜치

        // URL 정보
        private String url;         // API URL
        private String htmlUrl;     // 웹 URL
        private String diffUrl;     // Diff URL
        private String patchUrl;    // Patch URL

        // 통계 정보
        private Integer commits;         // 커밋 수
        private Integer additions;       // 추가된 라인 수
        private Integer deletions;       // 삭제된 라인 수
        private Integer changedFiles;    // 변경된 파일 수

        // getters and setters...
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Branch {
        private String label;       // "owner:branch_name"
        private String ref;         // "branch_name"
        private String sha;         // 커밋 SHA
        private User user;          // 브랜치 소유자
        private Repository repo;    // 저장소 정보

        // getters and setters...
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    public static class Repository {
        private Long id;
        private String nodeId;
        private String name;        // 저장소 이름
        private String fullName;    // "owner/repo"
        private Boolean isPrivate;  // 비공개 여부
        private User owner;         // 소유자
        private String htmlUrl;     // 웹 URL
        private String cloneUrl;    // Git 클론 URL
        private String defaultBranch; // 기본 브랜치

        // getters and setters...
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class User {
        private Long id;
        private String nodeId;
        private String login;       // 사용자명
        private String avatarUrl;   // 아바타 이미지 URL
        private String url;         // API URL
        private String htmlUrl;     // 프로필 URL
        private String type;        // "User" or "Bot"

        // getters and setters...
    }

    @Data
    public static class Installation {
        @JsonProperty("id")
        private Long id;

        @JsonProperty("node_id")
        private String nodeId;
    }
}
