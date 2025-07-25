package com.example.Veco.domain.team.service;

import com.example.Veco.domain.mapping.repository.MemberTeamRepository;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.domain.team.repository.TeamRepository;
import com.example.Veco.domain.workspace.converter.WorkspaceConverter;
import com.example.Veco.domain.workspace.dto.WorkspaceResponseDTO;
import com.example.Veco.domain.workspace.entity.WorkSpace;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TeamQueryServiceImpl implements TeamQueryService {

    private final TeamRepository teamRepository;
    private final MemberTeamRepository memberTeamRepository;

}

