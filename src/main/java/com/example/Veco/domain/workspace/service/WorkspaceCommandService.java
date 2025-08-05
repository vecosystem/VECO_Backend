package com.example.Veco.domain.workspace.service;

import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.workspace.dto.WorkspaceRequestDTO;
import com.example.Veco.domain.workspace.dto.WorkspaceResponseDTO;
import com.example.Veco.domain.workspace.entity.WorkSpace;
import com.example.Veco.global.auth.user.userdetails.CustomUserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface WorkspaceCommandService {

    WorkspaceResponseDTO.CreateTeamResponseDto createTeam(WorkSpace workSpace, WorkspaceRequestDTO.CreateTeamRequestDto request);

    void updateTeamOrder(WorkSpace workspace, List<Long> teamIdList);

    WorkspaceResponseDTO.JoinWorkspace joinWorkspace(WorkspaceRequestDTO.JoinWorkspace dto, CustomUserDetails user);

    WorkspaceResponseDTO.CreateWorkspaceResponseDto createWorkspace(Member member, WorkspaceRequestDTO.WorkspaceRequestDto request);
}
