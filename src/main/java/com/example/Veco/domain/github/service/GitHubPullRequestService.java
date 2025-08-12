package com.example.Veco.domain.github.service;

import com.example.Veco.domain.external.converter.ExternalConverter;
import com.example.Veco.domain.github.dto.webhook.GitHubPullRequestPayload;
import com.example.Veco.domain.external.entity.External;
import com.example.Veco.domain.external.exception.ExternalException;
import com.example.Veco.domain.github.exception.GitHubException;
import com.example.Veco.domain.external.exception.code.ExternalErrorCode;
import com.example.Veco.domain.github.exception.code.GitHubErrorCode;
import com.example.Veco.domain.external.repository.ExternalRepository;
import com.example.Veco.domain.mapping.GithubInstallation;
import com.example.Veco.domain.mapping.repository.GitHubInstallationRepository;
import com.example.Veco.domain.team.dto.NumberSequenceResponseDTO;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.domain.team.service.NumberSequenceService;
import com.example.Veco.global.enums.Category;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GitHubPullRequestService {

    private ObjectMapper objectMapper = new ObjectMapper();
    private final ExternalRepository externalRepository;
    private final GitHubInstallationRepository gitHubInstallationRepository;
    private final NumberSequenceService numberSequenceService;

    public void handlePullRequestEvent(String payload) {

        try{

            GitHubPullRequestPayload prPayload = objectMapper.readValue(payload, GitHubPullRequestPayload.class);

            switch (prPayload.getAction()) {
                case "opened":
                    handlePullRequestOpened(prPayload);
                    break;
                case "closed":
                    closeIssue(prPayload);
                    break;
                case "edited":
                    updateIssue(prPayload);
                    break;
                default:
                    log.debug("Unhandled PR action: {}", prPayload.getAction());
            }

        }catch (Exception e){

        }
    }

    private void handlePullRequestOpened(GitHubPullRequestPayload payload) {

        Long installationId = payload.getInstallation().getId();

        GithubInstallation installInfo = gitHubInstallationRepository.findByInstallationId(installationId)
                .orElseThrow(() -> new GitHubException(GitHubErrorCode.INSTALLATION_INFO_NOT_FOUND));

        Team team = installInfo.getTeam();

        NumberSequenceResponseDTO code =
                numberSequenceService.allocateNextNumber(team.getWorkSpace().getName(),
                        team.getId(),
                        Category.EXTERNAL);

        External external = ExternalConverter.byGitHubPullRequest(payload, installInfo.getTeam(), code.getNextCode());

        externalRepository.save(external);

    }

    private void closeIssue(GitHubPullRequestPayload payload) {
        External external = externalRepository.findByGithubDataId(payload.getPullRequest().getId())
                .orElseThrow(() -> new ExternalException(ExternalErrorCode.NOT_FOUND));

        external.closeIssue();
        externalRepository.save(external);
    }

    private void updateIssue(GitHubPullRequestPayload payload) {
        GitHubPullRequestPayload.PullRequest pullRequest = payload.getPullRequest();

        External external = externalRepository.findByGithubDataId(pullRequest.getId())
                .orElseThrow(() -> new ExternalException(ExternalErrorCode.NOT_FOUND));

        external.updateByPullRequest(pullRequest);

    }

}
