package com.example.Veco.domain.github.service;

import com.example.Veco.domain.github.dto.response.GitHubApiResponseDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
