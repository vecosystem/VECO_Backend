package com.example.Veco.domain.external.service;

import com.example.Veco.domain.external.converter.ExternalConverter;
import com.example.Veco.domain.external.dto.ExternalCursor;
import com.example.Veco.domain.external.dto.GitHubWebhookPayload;
import com.example.Veco.domain.external.entity.External;
import com.example.Veco.domain.external.entity.GitHubIssue;
import com.example.Veco.domain.external.repository.ExternalRepository;
import com.example.Veco.domain.external.repository.GitHubIssueRepository;
import com.example.Veco.domain.mapping.GithubInstallation;
import com.example.Veco.domain.mapping.repository.GitHubInstallationRepository;
import com.example.Veco.domain.team.dto.NumberSequenceResponseDTO;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.domain.team.enums.Category;
import com.example.Veco.domain.team.repository.TeamRepository;
import com.example.Veco.domain.team.service.NumberSequenceService;
import com.example.Veco.global.apiPayload.exception.VecoException;
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
    private final ExternalRepository externalRepository;
    private final NumberSequenceService numberSequenceService;
    private final GitHubInstallationRepository gitHubInstallationRepository;
    private final TeamRepository teamRepository;

    public void processIssueWebhook(GitHubWebhookPayload payload) {

        log.info("payload.getIssue: {}", payload.getIssue());
        log.info("paload.getRepository: {}", payload.getRepository());
        log.info("paload.getAction: {}", payload.getAction());
        log.info("paload.getSender: {}", payload.getSender());
        log.info("payload.getInstallation: {}", payload.getInstallation());

        String action = payload.getAction();

        GitHubWebhookPayload.Issue issue = payload.getIssue();

        switch (action) {
            case "opened":
                log.info("Issue {} opened", issue.getNumber());
                createIssue(payload);
                break;
            case "closed":
                log.info("Issue {} closed", issue.getNumber());
                closeIssue(payload);
                break;
            case "edited":
                log.info("Issue {} edited", issue.getNumber());
                updateIssue(payload);
        }
    }

    private void closeIssue(GitHubWebhookPayload payload) {
        External external = externalRepository.findByGithubDataId(payload.getIssue().getId())
                .orElseThrow(() -> new VecoException("Issue not found"));

        external.closeIssue();
        externalRepository.save(external);
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

        createVecoExternal(payload);

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

    private void createVecoExternal(GitHubWebhookPayload payload) {

        Long installationId = payload.getInstallation().getId();

        GithubInstallation installInfo = gitHubInstallationRepository.findByInstallationId(installationId)
                .orElseThrow(() -> new VecoException("Installation not found"));

        Team team = installInfo.getTeam();

        NumberSequenceResponseDTO code =
                numberSequenceService.allocateNextNumber(team.getWorkSpace().getName(),
                        team.getId(),
                        Category.EXTERNAL);

        External external = ExternalConverter.byGitHubIssue(payload, installInfo.getTeam(), code.getNextCode());

        externalRepository.save(external);
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
        External external = externalRepository.findByGithubDataId(issueData.getId())
                .orElseThrow(() -> new VecoException("Issue not found"));

        external.updateExternalByGithubIssue(issueData);

        externalRepository.save(external);

//        if (existingIssue.isEmpty()) {
//            log.warn("Issue not found for update: {}", issueData.getId());
//            createIssue(payload);
//            return;
//        }

//        GitHubIssue issue = existingIssue.get();
//        issue.setTitle(issueData.getTitle());
//        issue.setBody(issueData.getBody());
//        issue.setState(GitHubIssue.IssueState.valueOf(issueData.getState().toUpperCase()));
//        issue.setAssigneeLogin(issueData.getAssignee() != null ? issueData.getAssignee().getLogin() : null);
//        issue.setAssigneeId(issueData.getAssignee() != null ? issueData.getAssignee().getId() : null);
//        issue.setLabels(extractLabelNames(issueData.getLabels()));
//        issue.setGithubUpdatedAt(issueData.getUpdatedAt());
//        issue.setGithubClosedAt(issueData.getClosedAt());
//        issue.setCommentsCount(issueData.getComments());
//        issue.setLocked(issueData.getLocked());
//        issue.setLockReason(issueData.getActiveLockReason());
//
//        gitHubIssueRepository.save(issue);
//        log.info("Updated issue: #{} - {}", issue.getNumber(), issue.getTitle());
    }
}
