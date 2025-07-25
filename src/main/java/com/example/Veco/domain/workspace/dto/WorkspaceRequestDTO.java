package com.example.Veco.domain.workspace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class WorkspaceRequestDTO {

    /**
     * 팀 생성 요청 DTO
     * - 팀 이름, 멤버 ID 리스트를 전달받음
     */
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateTeamRequestDto {
        private String teamName;
        private List<Long> memberId;
    }

    /**
     *
     */
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamOrderRequestDto {
        private List<Long> teamIdList;
    }
}
