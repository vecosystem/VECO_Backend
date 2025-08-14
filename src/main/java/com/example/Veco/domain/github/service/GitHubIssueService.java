package com.example.Veco.domain.github.service;

import com.example.Veco.domain.external.converter.ExternalConverter;
import com.example.Veco.domain.github.converter.GitHubConverter;
import com.example.Veco.domain.github.dto.webhook.GitHubWebhookPayload;
import com.example.Veco.domain.external.dto.request.ExternalRequestDTO;
import com.example.Veco.domain.github.dto.response.GitHubApiResponseDTO;
import com.example.Veco.domain.external.entity.External;
import com.example.Veco.domain.github.entity.GitHubIssue;
import com.example.Veco.domain.external.exception.ExternalException;
import com.example.Veco.domain.github.exception.GitHubException;
import com.example.Veco.domain.external.exception.code.ExternalErrorCode;
import com.example.Veco.domain.github.exception.code.GitHubErrorCode;
import com.example.Veco.domain.external.repository.ExternalRepository;
import com.example.Veco.domain.github.repository.GitHubIssueRepository;
import com.example.Veco.domain.mapping.GithubInstallation;
import com.example.Veco.domain.mapping.repository.GitHubInstallationRepository;
import com.example.Veco.domain.team.dto.NumberSequenceResponseDTO;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.domain.team.service.NumberSequenceService;
import com.example.Veco.global.enums.Category;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class GitHubIssueService {

    private final GitHubIssueRepository gitHubIssueRepository;
    private final ExternalRepository externalRepository;
    private final NumberSequenceService numberSequenceService;
    private final GitHubInstallationRepository gitHubInstallationRepository;
    private ObjectMapper objectMapper = new ObjectMapper();
    private final GitHubTokenService gitHubTokenService;
    private final WebClient webClient = WebClient.builder()
            .baseUrl("https://api.github.com")
            .defaultHeader("Accept", "application/vnd.github+json")
            .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
            .defaultHeader("User-Agent", "VecoApp/1.0")
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024)) // 1MB
            .build();

    public void processIssueWebhook(String payload) {


        try{
            GitHubWebhookPayload issuePayload = objectMapper.readValue(payload, GitHubWebhookPayload.class);

            log.info("payload.getIssue: {}", issuePayload.getIssue());
            log.info("paload.getRepository: {}", issuePayload.getRepository());
            log.info("paload.getAction: {}", issuePayload.getAction());
            log.info("paload.getSender: {}", issuePayload.getSender());
            log.info("payload.getInstallation: {}", issuePayload.getInstallation());

            String action = issuePayload.getAction();

            GitHubWebhookPayload.Issue issue = issuePayload.getIssue();

            switch (action) {
                case "opened":
                    log.info("Issue {} opened", issue.getNumber());
                    createIssue(issuePayload);
                    break;
                case "closed":
                    log.info("Issue {} closed", issue.getNumber());
                    closeIssue(issuePayload);
                    break;
                case "edited":
                    log.info("Issue {} edited", issue.getNumber());
                    updateIssue(issuePayload);
            }
        }catch (Exception e){
            log.error(e.getMessage(), e);
        }
    }

    public void createGitHubIssue(ExternalRequestDTO.ExternalCreateRequestDTO requestDTO) {
       String token = gitHubTokenService.getInstallationToken(requestDTO.getInstallationId()).block();

        log.info("token: {}", token);

        try {
            // ✅ .block()으로 실제 실행하고 결과 대기
            GitHubApiResponseDTO.GitHubIssueResponseDTO issue = webClient.post()
                    .uri("/repos/{owner}/{repo}/issues", requestDTO.getOwner(), requestDTO.getRepo())
                    .header("Authorization", "Bearer " + token)
                    .bodyValue(GitHubConverter.toIssueCreateDTO(requestDTO))
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, response -> {
                        return response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("GitHub 이슈 생성 실패 (4xx): owner={}, repo={}, status={}, body={}",
                                            requestDTO.getOwner(), requestDTO.getRepo(), response.statusCode(), errorBody);
                                    return Mono.error(new RuntimeException("이슈 생성 실패: " + response.statusCode()));
                                });
                    })
                    .bodyToMono(GitHubApiResponseDTO.GitHubIssueResponseDTO.class)
                    .block();  // ✅ 실제 실행

            log.info("GitHub 이슈 생성 성공: owner={}, repo={}, issueNumber={}",
                    requestDTO.getOwner(), requestDTO.getRepo(), issue.getNumber());

        } catch (Exception e) {
            log.error("GitHub 이슈 생성 중 오류 발생: {}", e.getMessage(), e);
            throw e;
        }

        log.info("done");
    }

    private void closeIssue(GitHubWebhookPayload payload) {
        External external = externalRepository.findByGithubDataId(payload.getIssue().getId())
                .orElseThrow(() -> new ExternalException(ExternalErrorCode.NOT_FOUND));

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

//        GitHubIssue issue = GitHubIssue.builder()
//                .githubIssueId(issueData.getId())
//                .nodeId(issueData.getNodeId())
//                .number(issueData.getNumber())
//                .title(issueData.getTitle())
//                .body(issueData.getBody())
//                .state(GitHubIssue.IssueState.valueOf(issueData.getState().toUpperCase()))
//                .repositoryFullName(repoData.getFullName())
//                .repositoryUrl(repoData.getUrl())
//                .htmlUrl(issueData.getHtmlUrl())
//                .creatorLogin(issueData.getUser().getLogin())
//                .creatorId(issueData.getUser().getId())
//                .creatorAvatarUrl(issueData.getUser().getAvatarUrl())
//                .assigneeLogin(issueData.getAssignee() != null ? issueData.getAssignee().getLogin() : null)
//                .assigneeId(issueData.getAssignee() != null ? issueData.getAssignee().getId() : null)
//                .labels(extractLabelNames(issueData.getLabels()))
//                .commentsCount(issueData.getComments())
//                .locked(issueData.getLocked())
//                .lockReason(issueData.getActiveLockReason())
//                .build();
//
//        gitHubIssueRepository.save(issue);
//        log.info("Created new issue: #{} - {}", issue.getNumber(), issue.getTitle());
    }

    private void createVecoExternal(GitHubWebhookPayload payload) {

        Long installationId = payload.getInstallation().getId();

        GithubInstallation installInfo = gitHubInstallationRepository.findByInstallationId(installationId)
                .orElseThrow(() -> new GitHubException(GitHubErrorCode.INSTALLATION_INFO_NOT_FOUND));

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
                .orElseThrow(() -> new ExternalException(ExternalErrorCode.NOT_FOUND));

        external.updateExternalByGithubIssue(issueData);

        externalRepository.save(external);

    }
}
