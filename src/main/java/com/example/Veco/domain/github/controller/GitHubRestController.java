package com.example.Veco.domain.github.controller;

import com.example.Veco.domain.github.dto.response.GitHubApiResponseDTO;
import com.example.Veco.domain.github.dto.response.GitHubResponseDTO;
import com.example.Veco.domain.github.service.GitHubRepositoryService;
import com.example.Veco.domain.github.service.GitHubService;
import com.example.Veco.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class GitHubRestController implements GitHubSwaggerDocs {

    private final GitHubService gitHubService;
    private final GitHubRepositoryService gitHubRepositoryService;
    private static final String FRONTEND_URL = "http://localhost:5173";

    @Hidden
    @GetMapping("/github/installation/callback")
    public ResponseEntity<Void> callbackInstallation(
            @Parameter(description = "팀 ID") @RequestParam("state") Long state,
            @Parameter(description = "GitHub App 설치 ID") @RequestParam("installation_id") Long installationId
    ) {

        gitHubService.saveInstallationInfo(state, installationId);

        String redirectUrl = String.format(
                "%s/github/complete?teamId=%d",
                FRONTEND_URL, state
        );

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(redirectUrl))
                .build();
    }

    @GetMapping("/api/teams/{teamId}/github/repositories")
    public ApiResponse<List<GitHubApiResponseDTO.GitHubRepositoryDTO>> getRepositories(@PathVariable("teamId") Long teamId) {

        try {
            log.info("레포지토리 조회 시작: teamId={}", teamId);

            // 1. Installation ID 조회 (동기)
            Long installationId = gitHubService.getInstallationIdSync(teamId);
            log.info("Installation ID 조회 완료: {}", installationId);

            // 2. 레포지토리 목록 조회 (동기)
            List<GitHubApiResponseDTO.GitHubRepositoryDTO> repositories =
                    gitHubRepositoryService.getInstallationRepositoriesSync(installationId);

            log.info("레포지토리 조회 완료: {} 개", repositories.size());

            return ApiResponse.onSuccess(repositories);

        } catch (Exception e) {
            log.error("레포지토리 조회 실패: teamId={}", teamId, e);
            return ApiResponse.onFailure("REPO_FETCH_FAILED", "레포지토리 조회 실패: " + e.getMessage(), null);
        }
    }

    @GetMapping("/api/github/connect")
    public ApiResponse<?> connectGitHub(@RequestParam("teamId") Long teamId) {
        String appInstallUrl = String.format(
                "https://github.com/apps/VecoApp/installations/new?state=%s",
                teamId
        );

        return ApiResponse.onSuccess(appInstallUrl);
    }

    @GetMapping("/api/teams/{teamId}/github/installation")
    public ApiResponse<GitHubResponseDTO.GitHubAppInstallationDTO> getInstallation(@PathVariable("teamId") Long teamId) {
        return ApiResponse.onSuccess(gitHubService.getInstallationInfo(teamId));
    }
}

