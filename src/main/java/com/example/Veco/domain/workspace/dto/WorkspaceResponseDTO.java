package com.example.Veco.domain.workspace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class WorkspaceResponseDTO {

    /**
     * 워크 스페이스 기본 정보 응답 DTO
     * - 워크스페이스 이름, 로고, URL 포함
     */
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkspaceResponseDto {
        private String name;
        private String profileUrl;
        private String workspaceUrl;
        private List<WorkspaceMemberWithTeamsDto.TeamInfoDto> teams;
    }

    /**
     * 워크 스페이스 내 팀 리스트 조회 응답 DTO
     * - 페이지네이션 정보 포함
     */
    @Builder
    @Getter
    public static class WorkspaceTeamListDto {
        private List<WorkspaceTeamDto> teamList;
        Integer listSize;
        Integer totalPage;
        Long totalElements;
        Boolean isFirst;
        Boolean isLast;
    }

    /**
     * 워크 스페이스 내 단일 팀 정보 DTO
     * - 팀 리스트 안의 요소로 사용
     */
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkspaceTeamDto {
        private Long teamId;
        private String name;
        private String profileUrl;
        private int memberCount;
        private LocalDateTime createdAt;
    }

    /**
     * 팀 생성 응답 DTO
     * - 생성된 팀의 ID, 이름, 포함된 멤버 목록을 포함
     */
    @Builder
    @Getter
    @NoArgsConstructor
                                                                                                                                                                                                                                                                                                @AllArgsConstructor
    public static class CreateTeamResponseDto {
        private Long teamId;
        private String teamName;
        private List<MemberDto> members;

        /**
         * 팀에 속한 멤버 정보 DTO
         */
        @Getter
        @Builder
        @AllArgsConstructor
        public static class MemberDto {
            private Long memberId;
            private String memberName;
        }
    }

    @Getter
    @Builder
    public static class WorkspaceMemberWithTeamsDto {
        private Long memberId;
        private String email;
        private String name;
        private String profileUrl;
        private List<TeamInfoDto> teams;
        private LocalDateTime joinedAt;

        @Getter
        @Builder
        public static class TeamInfoDto {
            private Long teamId;
            private String teamName;
            private String teamProfileUrl;
        }
    }

    @Getter
    @Builder
    public static class PreviewUrlResponseDto {
        private String inviteUrl;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateWorkspaceResponseDto {
        private Long workspaceId;
        private String workspaceName;
        private String workspaceUrl;
        private String inviteUrl;
        private String invitePassword;
        private Long defaultTeamId;
    }
}
