package com.example.Veco.domain.external.dto;

import com.example.Veco.domain.team.dto.AssigneeResponseDTO;
import com.example.Veco.global.enums.ExtServiceType;
import com.example.Veco.global.enums.Priority;
import com.example.Veco.global.enums.State;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class ExternalResponseDTO {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Schema(description = "외부 이슈 응답 DTO")
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
        
        @Schema(description = "시작일", example = "2024-01-01")
        private LocalDate startDate;
        
        @Schema(description = "마감일", example = "2024-01-31")
        private LocalDate endDate;
        
        @Schema(description = "목표 ID", example = "1")
        private String goalId;
        
        @Schema(description = "목표 제목", example = "목표 제목")
        private String goalTitle;
        
        @Schema(description = "외부 이슈 코드", example = "EXT-001")
        private String name;

        @Schema(description = "연동된 외부 툴", example = "GITHUB")
        private ExtServiceType extServiceType;

        private AssigneeResponseDTO managers;
//
//        @Schema(description = "배정자 목록")
//        private List<AssigneeResponseDTO.AssigneeDTO> managers;
    }

    @Builder
    public static class AssigneeResponseDTO{
        private Integer cnt;
        List<AssigneeInfoDTO> info;
    }

    @Builder
    public static class AssigneeInfoDTO {
        private String profileUrl;
        private String nickname;
    }

    @Builder
    public static class DeadlineResposneDTO {
        private LocalDate start;
        private LocalDate end;
    }
}
