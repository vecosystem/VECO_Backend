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
import com.example.Veco.domain.workspace.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkspaceCommandServiceImpl implements WorkspaceCommandService {

    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;
    private final MemberTeamRepository memberTeamRepository;

    @Override
    public WorkspaceResponseDTO.CreateTeamResponseDto createTeam(WorkspaceRequestDTO.CreateTeamRequestDto request) {
        Team team = teamRepository.save(Team.builder()
                .name(request.getTeamName())
                .build());

        List<Member> members = memberRepository.findAllById(request.getMemberId());

        List<MemberTeam> memberTeams = members.stream()
                .map(member -> MemberTeam.builder()
                        .member(member)
                        .team(team)
                        .build())
                .toList();

        memberTeamRepository.saveAll(memberTeams);

        return WorkspaceConverter.toCreateTeamResponseDto(team, members);
    }
}
