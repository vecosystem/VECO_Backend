package com.example.Veco.domain.workspace.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
        @NotBlank(message = "팀 이름을 입력해주세요.")
        @Size(min = 4, max = 10, message = "팀 이름은 최소 4자, 최대 10자입니다.")
        private String teamName;

        @NotEmpty(message = "팀 멤버를 한 명 이상 선택해주세요.")
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
        //@NotEmpty("")
        private List<Long> teamIdList;
    }

    // 워크스페이스 참여
    public record JoinWorkspace(
            String token,
            String password
    ){}

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PreviewUrlRequestDto {
        @NotBlank(message = "워크스페이스 이름을 입력해주세요.")
        @Size(min = 4, max = 10, message = "워크스페이스 이름은 최소 4자, 최대 10자입니다.")
        private String workspaceName;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateWorkspaceRequestDto {
        @NotBlank(message = "워크스페이스 이름을 입력해주세요.")
        @Size(min = 4, max = 10, message = "워크스페이스 이름은 최소 4자, 최대 10자입니다.")
        private String workspaceName;
    }
}
