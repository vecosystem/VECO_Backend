package com.example.Veco.domain.external.controller;

import com.example.Veco.domain.external.dto.response.GitHubApiResponseDTO;
import com.example.Veco.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.PathVariable;
import reactor.core.publisher.Mono;

import java.util.List;

public interface GitHubSwaggerDocs {

    @Operation(
            summary = "GitHub App이 접근 가능한 레포지토리 목록 조회",
            description = "해당 팀이 설치한 GitHub App 이 접근 가능한 레포지토리 목록을 조회합니다."
    )
    Mono<ApiResponse<List<GitHubApiResponseDTO.GitHubRepositoryDTO>>> getRepositories(@PathVariable("teamId") Long teamId);

}
