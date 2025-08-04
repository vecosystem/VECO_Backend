package com.example.Veco.domain.workspace.converter;

import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.domain.workspace.dto.WorkspaceResponseDTO;
import com.example.Veco.domain.workspace.entity.WorkSpace;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WorkspaceConverter {

    /**
     * Workspace 기본 정보 응답 DTO 변환
     */
    public static WorkspaceResponseDTO.WorkspaceResponseDto toWorkspaceResponse(WorkSpace workspace) {
        return WorkspaceResponseDTO.WorkspaceResponseDto.builder()
                .workspaceId(workspace.getId())
                .workspaceName(workspace.getName())
                .workspaceImageUrl(workspace.getProfileUrl())
                .workspaceUrl(workspace.getWorkspaceUrl())
                .defaultTeamId(workspace.getTeams().get(0).getId())
                .build();
    }

    /**
     * 워크 스페이스 내 단일 팀 정보 DTO 변환
     */
    public static WorkspaceResponseDTO.WorkspaceTeamDto toWorkspaceTeamDto(Team team, int memberCount) {
        return WorkspaceResponseDTO.WorkspaceTeamDto.builder()
                .teamId(team.getId())
                .teamName(team.getName())
                .memberCount(memberCount)
                .createdAt(team.getCreatedAt())
                .build();
    }

    /**
     * 워크 스페이스 내 팀 리스트 조회 응답 DTO 변환
     * - 페이지네이션 된 Team + 각 팀별 멤버 수 Map
     */
    public static WorkspaceResponseDTO.WorkspaceTeamListDto toWorkspaceTeamListDto(Page<Team> teamPage, Map<Long, Integer> memberCountMap) {

        List<WorkspaceResponseDTO.WorkspaceTeamDto> list = teamPage.getContent().stream()
                .map(team -> toWorkspaceTeamDto(
                        team,
                        memberCountMap.getOrDefault(team.getId(), 0)))
                .collect(Collectors.toList());

        return WorkspaceResponseDTO.WorkspaceTeamListDto.builder()
                .isFirst(teamPage.isFirst())
                .isLast(teamPage.isLast())
                .totalPage(teamPage.getTotalPages())
                .totalElements(teamPage.getTotalElements())
                .listSize(list.size())
                .teamList(list)
                .build();
    }

    /**
     * 팀 생성 응답 DTO 변환
     * - Team + 멤버 리스트
     */
    public static WorkspaceResponseDTO.CreateTeamResponseDto toCreateTeamResponseDto(Team team, List<Member> members) {
        return WorkspaceResponseDTO.CreateTeamResponseDto.builder()
                .teamId(team.getId())
                .teamName(team.getName())
                .members(
                        members.stream()
                                .map(member -> WorkspaceResponseDTO.CreateTeamResponseDto.MemberDto.builder()
                                        .memberId(member.getId())
                                        .name(member.getName())
                                        .build())
                                .collect(Collectors.toList())
                        )
                        .build();
    }

    // 워크스페이스 참여
    public static WorkspaceResponseDTO.JoinWorkspace toJoinWorkspace(
            Long id,
            LocalDateTime now
    ){
        return WorkspaceResponseDTO.JoinWorkspace.builder()
                .workspaceId(id)
                .joinedAt(now)
                .build();
    }

    public static WorkspaceResponseDTO.PreviewUrlResponseDto toPreviewUrlResponseDto(String previewUrl) {
        return WorkspaceResponseDTO.PreviewUrlResponseDto.builder()
                .workspaceUrl(previewUrl)
                .build();
    }

    public static WorkspaceResponseDTO.InviteInfoResponseDto toInviteInfoResponseDto(WorkSpace workspace, Member member) {
        return WorkspaceResponseDTO.InviteInfoResponseDto.builder()
                .name(member.getName())
                .inviteUrl(workspace.getInviteUrl())
                .invitePassword(workspace.getInvitePassword())
                .build();
    }
}
