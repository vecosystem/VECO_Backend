package com.example.Veco.domain.workspace.converter;

import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.domain.workspace.dto.WorkspaceResponseDTO;
import com.example.Veco.domain.workspace.entity.WorkSpace;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WorkspaceConverter {

    public static WorkspaceResponseDTO.WorkspaceResponseDto toWorkspaceResponse(WorkSpace workspace) {
        return WorkspaceResponseDTO.WorkspaceResponseDto.builder()
                .name(workspace.getName())
                .profileUrl(workspace.getProfileUrl())
                .workspaceUrl(workspace.getWorkspaceUrl())
                .build();
    }

    public static WorkspaceResponseDTO.WorkspaceTeamDto toWorkspaceTeamDto(Team team, int memberCount) {
        return WorkspaceResponseDTO.WorkspaceTeamDto.builder()
                .teamId(team.getId())
                .name(team.getName())
                .memberCount(memberCount)
                .build();
    }

    public static WorkspaceResponseDTO.WorkspaceTeamListDto toWorkspaceTeamListDto(
            Page<Team> teamPage, Map<Long, Integer> memberCountMap) {

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

    public static WorkspaceResponseDTO.CreateTeamResponseDto toCreateTeamResponseDto(Team team, List<Member> members) {
        return WorkspaceResponseDTO.CreateTeamResponseDto.builder()
                .teamId(team.getId())
                .teamName(team.getName())
                .members(
                        members.stream()
                                .map(member -> WorkspaceResponseDTO.CreateTeamResponseDto.MemberDto.builder()
                                        .memberId(member.getId())
                                        .memberName(member.getName())
                                        .build())
                                .collect(Collectors.toList())
                        )
                        .build();
    }
}
