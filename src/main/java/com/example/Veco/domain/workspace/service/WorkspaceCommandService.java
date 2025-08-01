package com.example.Veco.domain.workspace.service;

import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.workspace.dto.WorkspaceRequestDTO;
import com.example.Veco.domain.workspace.dto.WorkspaceResponseDTO;
import com.example.Veco.domain.workspace.entity.WorkSpace;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface WorkspaceCommandService {

    WorkspaceResponseDTO.CreateTeamResponseDto createTeam(WorkSpace workSpace, WorkspaceRequestDTO.CreateTeamRequestDto request);

    void updateTeamOrder(WorkSpace workspace, List<Long> teamIdList);

    WorkspaceResponseDTO.CreateWorkspaceResponseDto createWorkspace(Member member, WorkspaceRequestDTO.CreateWorkspaceRequestDto request);
}
