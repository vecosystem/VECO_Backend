package com.example.Veco.domain.external.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class GitHubResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GitHubAppInstallationDTO {
        private Long teamId;
        private Long installationId;
    }
}
