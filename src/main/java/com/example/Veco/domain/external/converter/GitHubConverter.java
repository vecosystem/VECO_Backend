package com.example.Veco.domain.external.converter;

import com.example.Veco.domain.external.dto.response.GitHubResponseDTO;
import com.example.Veco.domain.mapping.GithubInstallation;

public class GitHubConverter {

    public static GitHubResponseDTO.GitHubAppInstallationDTO toGitHubAppInstallationDTO(GithubInstallation installation) {
        return GitHubResponseDTO.GitHubAppInstallationDTO.builder()
                .installationId(installation.getInstallationId())
                .teamId(installation.getTeam().getId())
                .build();
    }
}
