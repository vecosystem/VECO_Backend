package com.example.Veco.domain.workspace.service;

import com.example.Veco.domain.assignee.entity.Assignee;
import com.example.Veco.domain.assignee.repository.AssigneeRepository;
import com.example.Veco.domain.mapping.converter.MemberTeamConverter;
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
import com.example.Veco.global.auth.user.AuthUser;
import com.example.Veco.global.auth.user.userdetails.CustomUserDetails;
import com.example.Veco.global.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkspaceCommandServiceImpl implements WorkspaceCommandService {

    private final TeamRepository teamRepository;
    private final MemberRepository memberRepository;
    private final MemberTeamRepository memberTeamRepository;
    private final WorkspaceRepository workspaceRepository;
    private final AssigneeRepository assigneeRepository;
    private final SlugGenerator slugGenerator;
    private final InviteTokenGenerator inviteTokenGenerator;
    private final InvitePasswordGenerator invitePasswordGenerator;

    /**
     * 팀 생성 및 멤버 할당 로직
     */
    @Transactional
    @Override
    public WorkspaceResponseDTO.CreateTeamResponseDto createTeam(WorkSpace workspace, WorkspaceRequestDTO.CreateTeamRequestDto request) {
        // 1. 팀 이름 중복 검사
        if (teamRepository.existsByNameAndWorkSpace(request.getTeamName(), workspace)) {
            throw new TeamException(TeamErrorCode._DUPLICATE_TEAM_NAME);
        }
        int maxOrder = teamRepository.findMaxOrderByWorkSpace(workspace).orElse(-1);
        // 2. 팀 저장
        Team team = teamRepository.save(Team.builder()
                .name(request.getTeamName())
                .workSpace(workspace)
                .goalNumber(1L)
                .order(maxOrder + 1) // 새로 만든 팀은 자동으로 맨 뒤 배치
                .build());

        // 3. 요청으로 전달된 멤버 ID 리스트로 멤버 조회
        List<Member> members = memberRepository.findAllById(request.getMemberId());
        List<Long> memberIds = request.getMemberId();

        // 4-1. 에러 처리 : 멤버가 아예 없을 경우 예외 발생
        if (members.isEmpty()) {
            throw new MemberHandler(MemberErrorStatus._MEMBER_NOT_FOUND);
        }

        // 4-2 에러 처리 : 존재하지 않는 멤버 아이디가 포함되어 있을 경우
        if (members.size() != memberIds.size()) {
            throw new MemberHandler(MemberErrorStatus._INVALID_MEMBER_INCLUDE);
        }

        // 4-3. 에러 처리 : 워크스페이스에 속하지 않은 멤버가 있는지 체크
        boolean hasInvalidMember = members.stream()
                .anyMatch(member -> !workspace.equals(member.getWorkSpace()));

        if (hasInvalidMember) {
            throw new MemberHandler(MemberErrorStatus._MEMBER_NOT_IN_WORKSPACE);
        }

        // 5. 멤버와 팀을 매핑하여 MemberTeam 리스트 생성
        List<MemberTeam> memberTeams = members.stream()
                .map(member -> MemberTeam.builder()
                        .member(member)
                        .role(Role.USER)
                        .team(team)
                        .build())
                .toList();

        // 6. 멤버~팀 관계 저장
        memberTeamRepository.saveAll(memberTeams);

        // 7. 응답 DTO 생성 후 반환
        return WorkspaceConverter.toCreateTeamResponseDto(team, members);
    }

    @Transactional
    @Override
    public void updateTeamOrder(WorkSpace workspace, List<Long> teamIdList) {
        // 요청한 팀 개수와 워크스페이스 안에 있는 실제 팀의 개수가 같아야함.
        int currentTeamCount = teamRepository.countByWorkSpace(workspace);
        if (teamIdList.size() != currentTeamCount) {
            throw new TeamException(TeamErrorCode._TEAM_COUNT_MISMATCH);
        }

        Long defaultTeamId = teamRepository.findMinTeamIdByWorkSpace(workspace);

        if (!teamIdList.get(0).equals(defaultTeamId)) {
            throw new TeamException(TeamErrorCode._DEFAULT_TEAM_MUST_BE_FIRST);
        }

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
    public WorkspaceResponseDTO.CreateWorkspaceResponseDto createWorkspace(Member member, WorkspaceRequestDTO.WorkspaceRequestDto request) {
        // 1. 워크스페이스 이름 중복 검사
        if (workspaceRepository.existsByName(request.getWorkspaceName())) {
            throw new WorkspaceHandler(WorkspaceErrorStatus._DUPLICATE_WORKSPACE_NAME);
        }
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
                // 평문대신 BCrypto 사용해서 암호화 한뒤 저장해야 함
                .invitePassword(invitePassword)
                .inviteUrl(inviteUrl)
                .profileUrl("https://s3.ap-northeast-2.amazonaws.com/s3.veco/default/defalut-workspace.png")
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
                .goalNumber(1L)
                .profileUrl("https://s3.ap-northeast-2.amazonaws.com/s3.veco/default/defalut-workspace.png") // 기본 팀은 워크스페이스 이미지 사용
                .build();

        workSpace.getTeams().add(defaultTeam);

        MemberTeam memberTeam = MemberTeamConverter.toMemberTeam(member, defaultTeam);
        defaultTeam.getMemberTeams().add(memberTeam);

        // 7. 저장
        try {
            workspaceRepository.save(workSpace);
            teamRepository.save(defaultTeam);
            memberTeamRepository.save(memberTeam);
            memberRepository.save(member);
        } catch (Exception e) {
            throw new WorkspaceHandler(WorkspaceErrorStatus._WORKSPACE_SAVE_FAILED);
        }

        // 8. 응답
        return WorkspaceResponseDTO.CreateWorkspaceResponseDto.builder()
                .workspaceId(workSpace.getId())
                .name(member.getName())
                .workspaceName(workSpace.getName())
                .inviteUrl(workSpace.getInviteUrl())
                .invitePassword(workSpace.getInvitePassword())
                .defaultTeamId(defaultTeam.getId())
                .workspaceUrl(workSpace.getWorkspaceUrl())
                .build();
    }

    // 워크스페이스 연동 해제
    @Override
    @Transactional
    public String unlinkWorkspace(
            AuthUser user
    ) {
        // 유저 정보 조회
        Member member = memberRepository.findBySocialUid(user.getSocialUid())
                .orElseThrow(() -> new MemberHandler(MemberErrorStatus._MEMBER_NOT_FOUND));

        // 유저 - 팀 조회
        List<MemberTeam> result = memberTeamRepository.findAllByMemberIdAndTeamIn(
                member.getId(),
                member.getWorkSpace().getTeams()
        );
        List<Long> memberTeamIds = new ArrayList<>();
        result.forEach(memberTeam -> memberTeamIds.add(memberTeam.getId()));

        // 담당자 정보 null 처리
        List<Assignee> assignees = assigneeRepository.findAllByMemberTeamIdIn(memberTeamIds);
        assignees.forEach(assignee -> assignee.updateMemberTeam(null));

        // 유저 - 팀 삭제
        memberTeamRepository.deleteAll(result);

        // 유저 - 워크스페이스 null 처리
        member.updateWorkspace(null);

        return member.getName();
    }

    // 워크스페이스 참여
    @Override
    public WorkspaceResponseDTO.JoinWorkspace joinWorkspace(
            WorkspaceRequestDTO.JoinWorkspace dto,
            CustomUserDetails user
    ) {

        // 사용자 조회
        Member member = memberRepository.findBySocialUid(user.getSocialUid())
                .orElseThrow(() -> new MemberHandler(MemberErrorStatus._MEMBER_NOT_FOUND));

        // 워크스페이스 조회: 초대 토큰을 이용해서 조회
        WorkSpace workSpace = workspaceRepository.findByInviteToken(dto.token())
                .orElseThrow(() -> new WorkspaceHandler(WorkspaceErrorStatus._WORKSPACE_NOT_FOUND));

        // 사용자가 이미 워크스페이스에 속해있는지 검증
        if (member.getWorkSpace() != null) {
            throw new WorkspaceHandler(WorkspaceErrorStatus._WORKSPACE_DUPLICATED);
        }

        // 연동 전, 암호 확인
        if (!workSpace.getInvitePassword().equals(dto.password())){
            throw new WorkspaceHandler(WorkspaceErrorStatus._INVALIDED_PASSWORD);
        }

        LocalDateTime now = LocalDateTime.now();

        // 사용자 - 워크스페이스 연동
        member.updateWorkspace(workSpace);

        // 사용자 - 기본 팀 연동
        Team team = teamRepository.findFirstByWorkSpaceOrderById(workSpace);

        // 사용자 - 팀 중복 여부 확인, 없을때만 생성
        if (!memberTeamRepository.existsByMemberIdAndTeamId(member.getId(), team.getId())) {
            MemberTeam memberTeam = MemberTeamConverter.toMemberTeam(member, team);
            memberTeamRepository.save(memberTeam);
        }

        return WorkspaceConverter.toJoinWorkspace(workSpace.getId(), now);
    }
}
