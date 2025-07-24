package com.example.Veco.domain.workspace.service;

import com.example.Veco.domain.workspace.dto.WorkspaceRequestDTO;
import com.example.Veco.domain.workspace.dto.WorkspaceResponseDTO;
import org.springframework.stereotype.Service;

@Service
public interface WorkspaceCommandService {

    public WorkspaceResponseDTO.CreateTeamResponseDto createTeam(WorkspaceRequestDTO.CreateTeamRequestDto request);
}
