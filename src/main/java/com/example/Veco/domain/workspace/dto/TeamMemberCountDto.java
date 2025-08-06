package com.example.Veco.domain.workspace.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * WorkspaceMemberWithTeamsDto 내 TeamInfoDto에 추가됨
 */
@Getter
@Builder
@NoArgsConstructor
public class TeamMemberCountDto {
    private Long teamId;
    private Long memberCount;

    public TeamMemberCountDto(Long teamId, Long memberCount) {
        this.teamId = teamId;
        this.memberCount = memberCount;
    }
}
