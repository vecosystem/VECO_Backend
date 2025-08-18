package com.example.Veco.domain.github.service;

import com.example.Veco.domain.github.dto.response.GitHubApiResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GitHubRepositoryService {

    private final GitHubTokenService tokenService;

    WebClient webClient = WebClient.builder()
            .baseUrl("https://api.github.com")
            .defaultHeader("Accept", "application/vnd.github+json")
            .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
            .defaultHeader("User-Agent", "VecoApp/1.0")
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024)) // 1MB
            .build();

    public List<GitHubApiResponseDTO.GitHubRepositoryDTO> getInstallationRepositoriesSync(Long installationId) {
        try {
            log.info("레포지토리 조회 시작: installationId={}", installationId);

            // 1. Installation Token 조회 (동기)
            String token = tokenService.getInstallationTokenSync(installationId);
            log.info("Installation Token 조회 완료: installationId={}", installationId);

            // 2. 레포지토리 목록 조회 (동기)
            return fetchRepositoriesSync(token, installationId);

        } catch (Exception e) {
            log.error("레포지토리 조회 실패: installationId={}", installationId, e);
            throw new RuntimeException("레포지토리 조회 실패", e);
        }
    }

    private List<GitHubApiResponseDTO.GitHubRepositoryDTO> fetchRepositoriesSync(String token, Long installationId) {
        try {
            log.info("GitHub API 호출 시작: installationId={}", installationId);

            GitHubApiResponseDTO.GitHubRepositoryResponseDTO response = webClient.get()
                    .uri("/installation/repositories")
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, clientResponse -> {
                        log.error("GitHub API 에러 응답: status={}", clientResponse.statusCode());
                        return clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("GitHub API 에러 바디: {}", errorBody);
                                    return Mono.error(new RuntimeException(
                                            "GitHub API 에러: " + clientResponse.statusCode() + ", body: " + errorBody));
                                });
                    })
                    .bodyToMono(GitHubApiResponseDTO.GitHubRepositoryResponseDTO.class)
                    .timeout(Duration.ofSeconds(10))
                    .doOnSuccess(result -> log.info("GitHub API 응답 성공: installationId={}, totalCount={}",
                            installationId, result != null ? result.getTotalCount() : 0))
                    .doOnError(error -> log.error("GitHub API 호출 실패: installationId={}", installationId, error))
                    .block(); // 동기 처리

            if (response != null) {
                log.info("설치된 레포지토리 조회 완료: installationId={}, count={}",
                        installationId, response.getTotalCount());
                log.info("repository : {}", response.getRepositories());
                return response.getRepositories();
            } else {
                throw new RuntimeException("GitHub API 응답이 null입니다.");
            }

        } catch (Exception e) {
            log.error("fetchRepositoriesSync 실패: installationId={}", installationId, e);
            throw new RuntimeException("GitHub 레포지토리 조회 실패", e);
        }
    }

    public Mono<List<GitHubApiResponseDTO.GitHubRepositoryDTO>> getInstallationRepositories(Long installationId) {
        return tokenService.getInstallationToken(installationId)
                .flatMap(token -> fetchRepositories(token, installationId));
    }

    private Mono<List<GitHubApiResponseDTO.GitHubRepositoryDTO>> fetchRepositories(String token, Long installationId) {
        return webClient.get()
                .uri("/installation/repositories") // Installation 토큰으로 접근 가능한 레포지토리들
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(GitHubApiResponseDTO.GitHubRepositoryResponseDTO.class)
                .map(response -> {
                    log.info("설치된 레포지토리 조회 완료: installationId={}, count={}",
                            installationId, response.getTotalCount());
                    log.info("repository : {} " , response.getRepositories());
                    return response.getRepositories();
                });
    }

    /**
     * 특정 레포지토리 접근 권한 확인
     */
    public Mono<Boolean> hasRepositoryAccess(Long installationId, String owner, String repo) {
        return getInstallationRepositories(installationId)
                .map(repositories -> repositories.stream()
                        .anyMatch(r -> r.getOwner().getLogin().equals(owner) && r.getName().equals(repo))
                );
    }
}
