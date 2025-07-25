package com.example.Veco.domain.workspace.service;


import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.domain.workspace.dto.WorkspaceResponseDTO;
import com.example.Veco.domain.workspace.entity.WorkSpace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface WorkspaceQueryService {

    WorkSpace getWorkSpaceByMember(Member member);

    WorkspaceResponseDTO.WorkspaceTeamListDto getTeamListByWorkSpace(Pageable pageable, WorkSpace workspace);
}
