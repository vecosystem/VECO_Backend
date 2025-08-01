package com.example.Veco.domain.workspace.service;

import com.example.Veco.domain.mapping.entity.MemberTeam;
import com.example.Veco.domain.mapping.repository.MemberTeamRepository;
import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.member.error.MemberErrorStatus;
import com.example.Veco.domain.member.error.MemberHandler;
import com.example.Veco.domain.member.repository.MemberRepository;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.domain.team.exception.TeamException;
import com.example.Veco.domain.team.exception.code.TeamErrorCode;
import com.example.Veco.domain.team.repository.TeamRepository;
import com.example.Veco.domain.workspace.converter.WorkspaceConverter;
import com.example.Veco.domain.workspace.dto.WorkspaceRequestDTO;
import com.example.Veco.domain.workspace.dto.WorkspaceResponseDTO;
import com.example.Veco.domain.workspace.entity.WorkSpace;
import com.example.Veco.domain.workspace.error.WorkspaceErrorStatus;
import com.example.Veco.domain.workspace.error.WorkspaceHandler;
import com.example.Veco.domain.workspace.repository.WorkspaceRepository;
import com.example.Veco.domain.workspace.util.InvitePasswordGenerator;
import com.example.Veco.domain.workspace.util.InviteTokenGenerator;
import com.example.Veco.domain.workspace.util.SlugGenerator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkspaceCommandServiceImpl implements WorkspaceCommandService {

    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;
    private final MemberTeamRepository memberTeamRepository;
    private final WorkspaceRepository workspaceRepository;
    private final SlugGenerator slugGenerator;
    private final InviteTokenGenerator inviteTokenGenerator;
    private final InvitePasswordGenerator invitePasswordGenerator;

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

    @Transactional
    @Override
    public void updateTeamOrder(WorkSpace workspace, List<Long> teamIdList) {
        for (int i = 0; i < teamIdList.size(); i++) {
            Long teamId = teamIdList.get(i);
            Team team = teamRepository.findById(teamId)
                    .orElseThrow(() -> new TeamException(TeamErrorCode._NOT_FOUND));

            // 워크스페이스에 속한 팀인지 확인
            if (!team.getWorkSpace().equals(workspace)) {
                throw new TeamException(TeamErrorCode._TEAM_NOT_IN_WORKSPACE);
            }

            team.updateOrder(i); // 인덱스를 order로 저장
        }
    }

    @Override
    public WorkspaceResponseDTO.CreateWorkspaceResponseDto createWorkspace(Member member, WorkspaceRequestDTO.CreateWorkspaceRequestDto request) {
        // 2. 이미 워크스페이스가 있으면 예외
        if (member.getWorkSpace() != null) {
            throw new WorkspaceHandler(WorkspaceErrorStatus._WORKSPACE_DUPLICATED);
        }

        // 3. 슬러그, 토큰, 초대 암호 생성
        String slug = slugGenerator.generate(request.getWorkspaceName());
        String token = inviteTokenGenerator.generate();
        String invitePassword = invitePasswordGenerator.generate();
        String workspaceUrl = "https://veco-eight.vercel.app/" + slug;
        String inviteUrl = workspaceUrl + "/invite?token=" + token;

        // 4. 워크스페이스 생성
        WorkSpace workSpace = WorkSpace.builder()
                .name(request.getWorkspaceName())
                .slug(slug)
                .inviteToken(token)
                .invitePassword(invitePassword)
                .inviteUrl(inviteUrl)
                .workspaceUrl(workspaceUrl)
                .members(new ArrayList<>()) // 초기화
                .teams(new ArrayList<>())   // 초기화
                .build();

        // 5. 양방향 연관관계 설정
        workSpace.getMembers().add(member);
        member.setWorkSpace(workSpace);

        // 6. 기본 팀 생성
        Team defaultTeam = Team.builder()
                .name(workSpace.getName()) // 워크스페이스 이름과 같은 디폴트 팀
                .workSpace(workSpace)
                .build();

        MemberTeam memberTeam = MemberTeam.builder()
                .member(member)
                .team(defaultTeam)
                .build();

        workSpace.getTeams().add(defaultTeam);
        defaultTeam.getMemberTeams().add(memberTeam);

        // 7. 저장
        try {
            workspaceRepository.save(workSpace);
            teamRepository.save(defaultTeam);
            memberRepository.save(member);
        } catch (Exception e) {
            throw new WorkspaceHandler(WorkspaceErrorStatus._WORKSPACE_SAVE_FAILED);
        }

        // 8. 응답
        return WorkspaceResponseDTO.CreateWorkspaceResponseDto.builder()
                .workspaceId(workSpace.getId())
                .workspaceName(workSpace.getName())
                .inviteUrl(workSpace.getInviteUrl())
                .invitePassword(workSpace.getInvitePassword())
                .defaultTeamId(defaultTeam.getId())
                .workspaceUrl(workSpace.getWorkspaceUrl())
                .build();
    }
}
