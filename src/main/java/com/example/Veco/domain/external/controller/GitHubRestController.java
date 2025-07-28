package com.example.Veco.domain.external.controller;

import com.example.Veco.domain.external.dto.response.GitHubResponseDTO;
import com.example.Veco.domain.external.exception.code.GitHubSuccessCode;
import com.example.Veco.domain.external.service.GitHubService;
import com.example.Veco.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GitHubRestController {

    private final GitHubService gitHubService;

    @GetMapping("/github/installation/callback")
    public ApiResponse<GitHubResponseDTO.GitHubAppInstallationDTO> callbackInstallation(
            @Parameter(description = "팀 ID") @RequestParam("state") Long state,
            @Parameter(description = "GitHub App 설치 ID") @RequestParam("installation_id") Long installationId
    ) {
       return ApiResponse.onSuccess(GitHubSuccessCode.GITHUB_APP_INSTALL_SUCCESS,
               gitHubService.saveInstallationInfo(state, installationId));
    }
}
