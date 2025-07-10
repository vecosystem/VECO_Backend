package com.example.Veco.domain.external.service;

import com.example.Veco.domain.external.dto.GitHubWebhookPayload;
import com.example.Veco.domain.external.entity.GitHubIssue;
import com.example.Veco.domain.external.repository.GitHubIssueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GitHubIssueService {

    private final GitHubIssueRepository gitHubIssueRepository;

    public void processIssueWebhook(GitHubWebhookPayload payload) {

        log.info("payload: {}", payload);

        String action = payload.getAction();

        GitHubWebhookPayload.Issue issue = payload.getIssue();

        switch (action) {
            case "opened":
                log.info("Issue {} opened", issue.getNumber());
                createIssue(payload);
                break;

        }
    }

    private void createIssue(GitHubWebhookPayload payload) {
        GitHubWebhookPayload.Issue issueData = payload.getIssue();
        GitHubWebhookPayload.Repository repoData = payload.getRepository();

        // 중복 체크
        if (gitHubIssueRepository.existsById(issueData.getId())) {
            log.warn("Issue already exists: {}", issueData.getId());
            updateIssue(payload);
            return;
        }

        GitHubIssue issue = GitHubIssue.builder()
                .githubIssueId(issueData.getId())
                .nodeId(issueData.getNodeId())
                .number(issueData.getNumber())
                .title(issueData.getTitle())
                .body(issueData.getBody())
                .state(GitHubIssue.IssueState.valueOf(issueData.getState().toUpperCase()))
                .repositoryFullName(repoData.getFullName())
                .repositoryUrl(repoData.getUrl())
                .htmlUrl(issueData.getHtmlUrl())
                .creatorLogin(issueData.getUser().getLogin())
                .creatorId(issueData.getUser().getId())
                .creatorAvatarUrl(issueData.getUser().getAvatarUrl())
                .assigneeLogin(issueData.getAssignee() != null ? issueData.getAssignee().getLogin() : null)
                .assigneeId(issueData.getAssignee() != null ? issueData.getAssignee().getId() : null)
                .labels(extractLabelNames(issueData.getLabels()))
                .githubCreatedAt(issueData.getCreatedAt())
                .githubUpdatedAt(issueData.getUpdatedAt())
                .githubClosedAt(issueData.getClosedAt())
                .commentsCount(issueData.getComments())
                .locked(issueData.getLocked())
                .lockReason(issueData.getActiveLockReason())
                .build();

        gitHubIssueRepository.save(issue);
        log.info("Created new issue: #{} - {}", issue.getNumber(), issue.getTitle());
    }

    private List<String> extractLabelNames(List<GitHubWebhookPayload.Label> labels) {
        if(labels == null){
            return List.of();
        }

        return labels.stream().map(GitHubWebhookPayload.Label::getName).toList();
    }

    private void updateIssue(GitHubWebhookPayload payload) {
        GitHubWebhookPayload.Issue issueData = payload.getIssue();

        Optional<GitHubIssue> existingIssue = gitHubIssueRepository.findById(issueData.getId());
        if (existingIssue.isEmpty()) {
            log.warn("Issue not found for update: {}", issueData.getId());
            createIssue(payload);
            return;
        }

        GitHubIssue issue = existingIssue.get();
        issue.setTitle(issueData.getTitle());
        issue.setBody(issueData.getBody());
        issue.setState(GitHubIssue.IssueState.valueOf(issueData.getState().toUpperCase()));
        issue.setAssigneeLogin(issueData.getAssignee() != null ? issueData.getAssignee().getLogin() : null);
        issue.setAssigneeId(issueData.getAssignee() != null ? issueData.getAssignee().getId() : null);
        issue.setLabels(extractLabelNames(issueData.getLabels()));
        issue.setGithubUpdatedAt(issueData.getUpdatedAt());
        issue.setGithubClosedAt(issueData.getClosedAt());
        issue.setCommentsCount(issueData.getComments());
        issue.setLocked(issueData.getLocked());
        issue.setLockReason(issueData.getActiveLockReason());

        gitHubIssueRepository.save(issue);
        log.info("Updated issue: #{} - {}", issue.getNumber(), issue.getTitle());
    }
}
