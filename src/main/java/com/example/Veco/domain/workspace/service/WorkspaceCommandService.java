package com.example.Veco.domain.workspace.service;

import com.example.Veco.domain.workspace.dto.WorkspaceRequestDTO;
import com.example.Veco.domain.workspace.dto.WorkspaceResponseDTO;
import com.example.Veco.domain.workspace.entity.WorkSpace;
import org.springframework.stereotype.Service;

@Service
public interface WorkspaceCommandService {

    WorkspaceResponseDTO.CreateTeamResponseDto createTeam(WorkSpace workSpace, WorkspaceRequestDTO.CreateTeamRequestDto request);
}
