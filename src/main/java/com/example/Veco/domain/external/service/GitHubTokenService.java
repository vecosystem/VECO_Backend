package com.example.Veco.domain.external.service;

import com.example.Veco.domain.external.dto.response.GitHubTokenResponse;
import com.example.Veco.domain.external.util.GitHubJwtProvider;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
@Slf4j
public class GitHubTokenService {

    private final GitHubJwtProvider jwtProvider;

    WebClient webClient = WebClient.builder()
            .baseUrl("https://api.github.com")
            .defaultHeader("Accept", "application/vnd.github+json")
            .defaultHeader("X-GitHub-Api-Version", "2022-11-28")
            .defaultHeader("User-Agent", "VecoApp/1.0")
            .codecs(configurer -> configurer.defaultCodecs().maxInMemorySize(1024 * 1024)) // 1MB
            .build();

    // 토큰 캐시 (실제로는 Redis나 DB 사용 권장)
    private final Map<Long, TokenInfo> tokenCache = new ConcurrentHashMap<>();

    /**
     * Installation ID로 유효한 Access Token 획득
     */
    public Mono<String> getInstallationToken(Long installationId) {
        TokenInfo tokenInfo = tokenCache.get(installationId);

        // 토큰이 없거나 5분 내에 만료되면 새로 발급
        if (tokenInfo == null || isTokenExpiringSoon(tokenInfo)) {
            return generateNewToken(installationId)
                    .doOnNext(token -> tokenCache.put(installationId, token))
                    .map(TokenInfo::getToken);
        }

        return Mono.just(tokenInfo.getToken());
    }

    /**
     * 동기 방식으로 토큰 획득 (기존 코드와의 호환성)
     */
    public String getInstallationTokenSync(Long installationId) {
        return getInstallationToken(installationId).block();
    }

    private Mono<TokenInfo> generateNewToken(Long installationId) {
        String jwt = jwtProvider.generateJwt();

        return webClient.post()
                .uri("/app/installations/{installationId}/access_tokens", installationId)
                .header("Authorization", "Bearer " + jwt)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> {
                    return response.bodyToMono(String.class)
                            .flatMap(errorBody -> {
                                log.error("GitHub API 4xx 오류: installationId={}, status={}, body={}",
                                        installationId, response.statusCode(), errorBody);
                                return Mono.error(new RuntimeException("GitHub API 클라이언트 오류: " + response.statusCode()));
                            });
                })
                .onStatus(HttpStatusCode::is5xxServerError, response -> {
                    log.error("GitHub API 5xx 오류: installationId={}, status={}",
                            installationId, response.statusCode());
                    return Mono.error(new RuntimeException("GitHub API 서버 오류: " + response.statusCode()));
                })
                .bodyToMono(GitHubTokenResponse.class)
                .map(response -> {
                    log.info("GitHub Installation Token 발급 성공: installationId={}", installationId);
                    return new TokenInfo(
                            response.getToken(),
                            System.currentTimeMillis() + 3600000 // 1시간 후 만료
                    );
                })
                .doOnError(error -> {
                    log.error("GitHub Installation Token 발급 실패: installationId={}, error={}",
                            installationId, error.getMessage());
                });
    }

    private boolean isTokenExpiringSoon(TokenInfo tokenInfo) {
        return System.currentTimeMillis() + 300000 >= tokenInfo.getExpiresAt(); // 5분 전
    }

    @Data
    @AllArgsConstructor
    public static class TokenInfo {
        private String token;
        private long expiresAt;
    }
}
