package com.example.Veco.domain.external.dto.response;

import com.example.Veco.global.enums.ExtServiceType;
import com.example.Veco.global.enums.Priority;
import com.example.Veco.global.enums.State;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ExternalResponseDTO {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SimpleListDTO {
        private int cnt;
        private List<SimpleExternalDTO> info;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SimpleExternalDTO {
        private Long id;
        private String title;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Schema(description = "외부 이슈 응답 DTO")
    public static class ExternalInfoDTO {
        @Schema(description = "이슈 ID", example = "1")
        private Long id;

        @Schema(description = "외부 이슈 코드", example = "EXT-001")
        private String name;

        @Schema(description = "이슈 제목", example = "외부 이슈 제목")
        private String title;

        @Schema(description = "이슈 설명", example = "이슈에 대한 상세 설명")
        private String content;

        @Schema(description = "우선순위", example = "HIGH")
        private Priority priority;

        @Schema(description = "이슈 상태", example = "TODO")
        private State state;

        @Schema(description = "목표 ID", example = "1")
        private Long goalId;

        @Schema(description = "목표 제목", example = "목표 제목")
        private String goalTitle;


        @Schema(description = "연동된 외부 툴", example = "GITHUB")
        private ExtServiceType extServiceType;

        private AssigneeResponseDTO managers;

        private DeadlineResponseDTO deadline;

        private ExternalCommentResponseDTO comments;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class LinkInfoResponseDTO {
        private Boolean linkedWithGithub;
        private Boolean linkedWithSlack;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ExternalDTO {
        @Schema(description = "이슈 ID", example = "1")
        private Long id;

        @Schema(description = "이슈 제목", example = "외부 이슈 제목")
        private String title;

        @Schema(description = "이슈 설명", example = "이슈에 대한 상세 설명")
        private String content;

        @Schema(description = "우선순위", example = "HIGH")
        private Priority priority;

        @Schema(description = "이슈 상태", example = "TODO")
        private State state;

        @Schema(description = "목표 ID", example = "1")
        private Long goalId;

        @Schema(description = "목표 제목", example = "목표 제목")
        private String goalTitle;

        @Schema(description = "외부 이슈 코드", example = "EXT-001")
        private String name;

        @Schema(description = "연동된 외부 툴", example = "GITHUB")
        private ExtServiceType extServiceType;

        private AssigneeResponseDTO managers;

        private DeadlineResponseDTO deadline;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CreateResponseDTO {
        private Long externalId;
        private LocalDateTime createdAt;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UpdateResponseDTO {
        private Long externalId;
        private LocalDateTime updatedAt;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class AssigneeResponseDTO{
        private Integer cnt;
        List<AssigneeInfoDTO> info;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class AssigneeInfoDTO {
        private String profileUrl;
        private String nickname;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class DeadlineResponseDTO {
        private LocalDate start;
        private LocalDate end;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ExternalCommentResponseDTO {
        private Integer cnt;
        List<CommentResponseDTO> info;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CommentResponseDTO {
        private String profileUrl;
        private String name;
        private LocalDateTime createdAt;
        private String content;
    }
}
