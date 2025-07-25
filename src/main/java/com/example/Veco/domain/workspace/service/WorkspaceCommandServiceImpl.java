package com.example.Veco.domain.workspace.service;

import com.example.Veco.domain.mapping.entity.MemberTeam;
import com.example.Veco.domain.mapping.repository.MemberTeamRepository;
import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.member.error.MemberErrorStatus;
import com.example.Veco.domain.member.error.MemberHandler;
import com.example.Veco.domain.member.repository.MemberRepository;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.domain.team.repository.TeamRepository;
import com.example.Veco.domain.workspace.converter.WorkspaceConverter;
import com.example.Veco.domain.workspace.dto.WorkspaceRequestDTO;
import com.example.Veco.domain.workspace.dto.WorkspaceResponseDTO;
import com.example.Veco.domain.workspace.entity.WorkSpace;
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

    /**
     * 팀 생성 및 멤버 할당 로직
     * 1. 팀 저장
     * 2. 멤버 ID로 멤버 조회
     * 3. MemberTeam 엔티티로 연결
     * 4. 연결 저장 후 DTO 반환
     */
    @Override
    public WorkspaceResponseDTO.CreateTeamResponseDto createTeam(WorkSpace workspace, WorkspaceRequestDTO.CreateTeamRequestDto request) {
        // 1. 팀 저장
        Team team = teamRepository.save(Team.builder()
                .name(request.getTeamName())
                .workSpace(workspace)
                .build());

        // 2. 요청으로 전달된 멤버 ID 리스트로 멤버 조회
        List<Member> members = memberRepository.findAllById(request.getMemberId());

        // 3. 에러 처리 : 멤버가 아예 없을 경우 예외 발생
        if (members.isEmpty()) {
            throw new MemberHandler(MemberErrorStatus._MEMBER_NOT_FOUND);
        }

        // 4. 멤버와 팀을 매핑하여 MemberTeam 리스트 생성
        List<MemberTeam> memberTeams = members.stream()
                .map(member -> MemberTeam.builder()
                        .member(member)
                        .team(team)
                        .build())
                .toList();

        // 5. 멤버-팀 관계 저장
        memberTeamRepository.saveAll(memberTeams);

        // 6. 응답 DTO 생성 후 반환
        return WorkspaceConverter.toCreateTeamResponseDto(team, members);
    }
}
