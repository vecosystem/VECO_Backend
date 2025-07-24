package com.example.Veco.domain.workspace.service;

import com.example.Veco.domain.mapping.entity.MemberTeam;
import com.example.Veco.domain.mapping.repository.MemberTeamRepository;
import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.member.repository.MemberRepository;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.domain.team.repository.TeamRepository;
import com.example.Veco.domain.workspace.converter.WorkspaceConverter;
import com.example.Veco.domain.workspace.dto.WorkspaceRequestDTO;
import com.example.Veco.domain.workspace.dto.WorkspaceResponseDTO;
import com.example.Veco.domain.workspace.entity.WorkSpace;
import com.example.Veco.domain.workspace.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkspaceQueryServiceImpl implements WorkspaceQueryService {

    WorkspaceRepository workspaceRepository;
    TeamRepository teamRepository;
    MemberTeamRepository memberTeamRepository;
    MemberRepository memberRepository;

    @Override
    public WorkSpace getWorkSpaceByMember(Member member) {
        return member.getWorkSpace();
    }

    @Override
    public WorkspaceResponseDTO.WorkspaceTeamListDto getTeamListByWorkSpace(Pageable pageable, WorkSpace workspace) {
        Page<Team> teamPage = teamRepository.findAllByWorkSpace(workspace, pageable);

        List<Long> teamIds = teamPage.getContent().stream()
                .map(Team::getId)
                .collect(Collectors.toList());

        List<Object[]> result = memberTeamRepository.countMembersByTeamIds(teamIds);

        Map<Long, Integer> memberCountMap = result.stream()
                .collect(Collectors.toMap(
                        r -> (Long) r[0],
                        r -> ((Long) r[1]).intValue()
                ));

        return WorkspaceConverter.toWorkspaceTeamListDto(teamPage, memberCountMap);
    }
}
