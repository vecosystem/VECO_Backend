package com.example.Veco.domain.workspace.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PreviewUrlRequestDto {
        @NotBlank(message = "워크스페이스 이름을 입력해주세요.")
        private String workspaceName;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateWorkspaceRequestDto {
        @NotBlank(message = "워크스페이스 이름을 입력해주세요.")
        private String workspaceName;
    }
}
