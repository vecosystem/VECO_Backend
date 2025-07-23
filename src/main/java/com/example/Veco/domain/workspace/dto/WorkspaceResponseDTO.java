package com.example.Veco.domain.workspace.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class WorkspaceResponseDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkspaceResponseDto {
        private String name;
        private String profileUrl;
        private String workspaceUrl;
    }

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
}
