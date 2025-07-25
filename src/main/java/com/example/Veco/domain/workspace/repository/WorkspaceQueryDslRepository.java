package com.example.Veco.domain.workspace.repository;

import com.example.Veco.domain.workspace.dto.WorkspaceResponseDTO;
import com.example.Veco.domain.workspace.entity.WorkSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface WorkspaceQueryDslRepository {

    List<WorkspaceResponseDTO.WorkspaceMemberWithTeamsDto> findWorkspaceMembersWithTeams(WorkSpace workspace);
}
