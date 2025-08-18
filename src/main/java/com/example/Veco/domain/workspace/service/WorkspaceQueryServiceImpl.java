package com.example.Veco.domain.workspace.service;

import com.example.Veco.domain.mapping.repository.MemberTeamRepository;
import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.member.repository.MemberRepository;
import com.example.Veco.domain.member.service.MemberQueryService;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.domain.team.repository.TeamRepository;
import com.example.Veco.domain.workspace.converter.WorkspaceConverter;
import com.example.Veco.domain.workspace.dto.TeamMemberCountDto;
import com.example.Veco.domain.workspace.dto.WorkspaceResponseDTO;
import com.example.Veco.domain.workspace.entity.WorkSpace;
import com.example.Veco.domain.workspace.error.WorkspaceErrorStatus;
import com.example.Veco.domain.workspace.error.WorkspaceHandler;
import com.example.Veco.domain.workspace.repository.WorkspaceQueryDslRepository;
import com.example.Veco.domain.workspace.repository.WorkspaceRepository;
import com.example.Veco.domain.workspace.util.SlugGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class WorkspaceQueryServiceImpl implements WorkspaceQueryService {

    private final WorkspaceRepository workspaceRepository;
    private final TeamRepository teamRepository;
    private final MemberTeamRepository memberTeamRepository;
    private final MemberRepository memberRepository;
    private final WorkspaceQueryDslRepository workspaceQueryDslRepository;
    private final SlugGenerator slugGenerator;
    private final MemberQueryService memberQueryService;

    /**
     * 로그인한 멤버가 속한 워크스페이스 조회
     * - 워크스페이스가 없으면 예외 처리
     */
    @Override
    public WorkSpace getWorkSpaceByMember(Member member) {
        return Optional.ofNullable(member.getWorkSpace())
                .orElseThrow(() -> new WorkspaceHandler(WorkspaceErrorStatus._WORKSPACE_NOT_FOUND));
    }

    @Override
    public WorkSpace getWorkspaceBySocialUid(String socialUid) {
        Member member = memberQueryService.getMemberBySocialUid(socialUid);
        return getWorkSpaceByMember(member);
    }

    /**
     * 특정 워크스페이스에 속한 팀 리스트를 페이징하여 조회
     * - 각 팀별 멤버 수 계산 포함
     * - workspace가 null일 경우 예외 발생
     */
    @Override
    public WorkspaceResponseDTO.WorkspaceTeamListDto getTeamListByWorkSpace(Pageable pageable, WorkSpace workspace) {
        if (workspace == null) {
            throw new WorkspaceHandler(WorkspaceErrorStatus._WORKSPACE_NOT_FOUND);
        }

        // 워크스페이스에 속한 팀들 조회 (페이징)
        Page<Team> teamPage = teamRepository.findAllByWorkSpace(workspace, pageable);

        // 팀 ID 리스트 추출
        List<Long> teamIds = teamPage.getContent().stream()
                .map(Team::getId)
                .collect(Collectors.toList());

        // 각 팀별 멤버 수 조회 (count)
        List<TeamMemberCountDto> result = memberTeamRepository.countMembersByTeamIds(teamIds);

        // 팀 ID → 멤버 수로 매핑
        Map<Long, Integer> memberCountMap = result.stream()
                .collect(Collectors.toMap(
                        TeamMemberCountDto::getTeamId,
                        dto -> dto.getMemberCount().intValue()
                ));

        // DTO로 변환하여 반환
        return WorkspaceConverter.toWorkspaceTeamListDto(teamPage, memberCountMap);
    }

    @Override
    public List<WorkspaceResponseDTO.WorkspaceMemberWithTeamsDto> getWorkspaceMembers(Member loginMember) {
        WorkSpace workspace = getWorkSpaceByMember(loginMember);

        return workspaceQueryDslRepository.findWorkspaceMembersWithTeams(workspace);
    }

    @Override
    public String createPreviewUrl(String workspaceName) {
        String slug = slugGenerator.generate(workspaceName);

        return "https://web.vecoservice.shop/" + slug ;
    }
}