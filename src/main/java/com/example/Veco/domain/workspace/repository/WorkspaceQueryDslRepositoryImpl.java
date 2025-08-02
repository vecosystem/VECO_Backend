package com.example.Veco.domain.workspace.repository;

import com.example.Veco.domain.mapping.entity.QMemberTeam;
import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.member.entity.QMember;
import com.example.Veco.domain.member.error.MemberErrorStatus;
import com.example.Veco.domain.member.error.MemberHandler;
import com.example.Veco.domain.team.entity.QTeam;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.domain.workspace.dto.WorkspaceResponseDTO;
import com.example.Veco.domain.workspace.entity.WorkSpace;
import com.example.Veco.domain.workspace.error.WorkspaceErrorStatus;
import com.example.Veco.domain.workspace.error.WorkspaceHandler;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
public class WorkspaceQueryDslRepositoryImpl implements WorkspaceQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<WorkspaceResponseDTO.WorkspaceMemberWithTeamsDto> findWorkspaceMembersWithTeams(WorkSpace workspace) {

        if (workspace == null) {
            throw new WorkspaceHandler(WorkspaceErrorStatus._WORKSPACE_NOT_FOUND);
        }

        QMemberTeam mt = QMemberTeam.memberTeam;
        QMember m = QMember.member;
        QTeam t = QTeam.team;

        // 1. 먼저 전체 MemberTeam 정보를 가져오되, 필요한 멤버/팀 정보만 projection
        List<Tuple> tuples = queryFactory
                .select(m, t, mt.createdAt)
                .from(mt)
                .join(mt.member, m)
                .join(mt.team, t)
                .where(m.workSpace.eq(workspace))
                .fetch();

        // 워크스페이스 내에 멤버가 없을 경우 예외 처리
        if (tuples.isEmpty()) {
            throw new MemberHandler(MemberErrorStatus._MEMBER_NOT_FOUND);
        }

        // 2. 멤버 ID 기준으로 그룹핑
        Map<Long, List<Tuple>> grouped = tuples.stream()
                .collect(Collectors.groupingBy(tuple -> tuple.get(m).getId()));

        // 3. 최종 DTO 변환
        return grouped.values().stream().map(tupleList -> {
            Member member = tupleList.get(0).get(m);

            List<WorkspaceResponseDTO.WorkspaceMemberWithTeamsDto.TeamInfoDto> teamDtos = tupleList.stream()
                    .map(tu -> {
                        Team team = tu.get(t);
                        return WorkspaceResponseDTO.WorkspaceMemberWithTeamsDto.TeamInfoDto.builder()
                                .teamId(team.getId())
                                .teamName(team.getName())
                                .teamImageUrl(team.getProfileUrl())
                                .build();
                    }).toList();

            LocalDateTime joinedAt = tupleList.stream()
                    .map(tu -> tu.get(mt.createdAt))
                    .min(LocalDateTime::compareTo)
                    .orElse(null);

            return WorkspaceResponseDTO.WorkspaceMemberWithTeamsDto.builder()
                    .memberId(member.getId())
                    .email(member.getEmail())
                    .name(member.getName())
                    .profileImageUrl(member.getProfile().getProfileImageUrl()) // 필요시 profile.getUrl()로 수정
                    .teams(teamDtos)
                    .joinedAt(joinedAt)
                    .build();
        }).toList();
    }
}
