package com.example.Veco.domain.workspace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class WorkspaceResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkspaceResponseDto {
        private String name;
        private String profileUrl;
        private String workspaceUrl;
    }
}
