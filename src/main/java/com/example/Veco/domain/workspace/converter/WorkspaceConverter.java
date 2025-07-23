package com.example.Veco.domain.workspace.converter;

import com.example.Veco.domain.workspace.dto.WorkspaceResponseDTO;
import com.example.Veco.domain.workspace.entity.WorkSpace;

public class WorkspaceConverter {

    public static WorkspaceResponseDTO.WorkspaceResponseDto toWorkspaceResponse(WorkSpace workspace) {
        return WorkspaceResponseDTO.WorkspaceResponseDto.builder()
                .name(workspace.getName())
                .profileUrl(workspace.getProfileUrl())
                .workspaceUrl(workspace.getWorkspaceUrl())
                .build();
    }
}
