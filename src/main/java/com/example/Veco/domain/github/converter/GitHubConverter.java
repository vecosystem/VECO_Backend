package com.example.Veco.domain.github.converter;

import com.example.Veco.domain.external.dto.request.ExternalRequestDTO;
import com.example.Veco.domain.github.dto.request.GitHubApiRequestDTO;
import com.example.Veco.domain.github.dto.response.GitHubResponseDTO;
import com.example.Veco.domain.mapping.GithubInstallation;

public class GitHubConverter {

    public static GitHubResponseDTO.GitHubAppInstallationDTO toGitHubAppInstallationDTO(GithubInstallation installation) {
        return GitHubResponseDTO.GitHubAppInstallationDTO.builder()
                .installationId(installation.getInstallationId())
                .teamId(installation.getTeam().getId())
                .build();
    }

    public static GitHubApiRequestDTO.IssueCreateRequestDTO toIssueCreateDTO(ExternalRequestDTO.ExternalCreateRequestDTO requestDTO) {
        return GitHubApiRequestDTO.IssueCreateRequestDTO.builder()
                .title(requestDTO.getTitle())
                .body(requestDTO.getContent())
                .assignees( new String[0])
                .labels(new String[0])
                .milestone(null)
                .build();
    }
}
