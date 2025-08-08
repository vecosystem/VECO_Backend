package com.example.Veco.domain.external.controller;

import com.example.Veco.domain.external.dto.response.GitHubApiResponseDTO;
import com.example.Veco.domain.external.dto.response.GitHubResponseDTO;
import com.example.Veco.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

import java.util.List;

@Tag(name = "GitHub 연동 API", description = "GitHub 외부 툴 연동을 위한 인증 및 콜백 API")
public interface GitHubSwaggerDocs {

    @Operation(
            summary = "GitHub App이 접근 가능한 레포지토리 목록 조회",
            description = "해당 팀이 설치한 GitHub App 이 접근 가능한 레포지토리 목록을 조회합니다."
    )
    Mono<ApiResponse<List<GitHubApiResponseDTO.GitHubRepositoryDTO>>> getRepositories(@PathVariable("teamId") Long teamId);

    @Operation(
            summary = "GitHub 연동을 위한 App 설치 페이지 URL 조회",
            description = "GitHub App 설치를 위한 App 설치 페이지 URL을 전달합니다."
    )
    ApiResponse<?> connectGitHub(@RequestParam("teamId") Long teamId);

    @Operation(
            summary = "GitHub 연동을 마친 팀의 연동 ID를 조회",
            description = "GitHub 연동을 성공적으로 마친 팀의 ID르 통해서 연동 ID를 조회합니다."
    )
    ApiResponse<GitHubResponseDTO.GitHubAppInstallationDTO> getInstallation(@PathVariable("teamId") Long teamId);
}
