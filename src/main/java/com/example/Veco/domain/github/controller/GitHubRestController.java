package com.example.Veco.domain.github.controller;

import com.example.Veco.domain.github.dto.response.GitHubApiResponseDTO;
import com.example.Veco.domain.github.dto.response.GitHubResponseDTO;
import com.example.Veco.domain.github.service.GitHubRepositoryService;
import com.example.Veco.domain.github.service.GitHubService;
import com.example.Veco.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
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
    public Mono<ApiResponse<List<GitHubApiResponseDTO.GitHubRepositoryDTO>>> getRepositories(@PathVariable("teamId") Long teamId) {

        // owner, repo 정보 추출해서 클라이언트에 제공
        return gitHubService.getInstallationIdAsync(teamId)  // 비동기 DB 조회
                .flatMap(gitHubRepositoryService::getInstallationRepositories)
                .map(ApiResponse::onSuccess)
                .onErrorReturn(ApiResponse.onFailure("REPO_FETCH_FAILED", "레포지토리 조회 실패", null));
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

