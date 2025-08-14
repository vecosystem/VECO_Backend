package com.example.Veco.domain.external.dto.response;

import com.example.Veco.global.enums.ExtServiceType;
import com.example.Veco.global.enums.State;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

public class ExternalGroupedResponseDTO {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Schema(description = "외부이슈 그룹화된 페이지네이션 응답")
    public static class ExternalGroupedPageResponse {
        @Schema(description = "상태별 그룹화된 데이터")
        private List<FilteredExternalGroup> data;
        
        @Schema(description = "다음 페이지 존재 여부")
        private boolean hasNext;
        
        @Schema(description = "다음 페이지 커서")
        private String nextCursor;
        
        @Schema(description = "페이지 크기")
        private int pageSize;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Schema(description = "상태별 외부이슈 그룹")
    public static class FilteredExternalGroup {
        @Schema(description = "필터 이름 (상태)")
        private String filterName;
        
        @Schema(description = "해당 상태의 데이터 개수")
        private int dataCnt;
        
        @Schema(description = "외부이슈 목록")
        private List<ExternalItemDTO> externals;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Schema(description = "외부이슈 항목")
    public static class ExternalItemDTO {
        @Schema(description = "이슈 ID")
        private Long id;
        
        @Schema(description = "외부 이슈 코드")
        private String name;
        
        @Schema(description = "이슈 제목")
        private String title;
        
        @Schema(description = "이슈 상태")
        private State state;
        
        @Schema(description = "우선순위")
        private String priority;
        
        @Schema(description = "마감일")
        private DeadlineDTO deadline;
        
        @Schema(description = "담당자 정보")
        private ManagersDTO managers;

        @Schema(description = "외부 연동 툴 정보")
        private ExtServiceType extServiceType;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Schema(description = "마감일 정보")
    public static class DeadlineDTO {
        @Schema(description = "시작일")
        private String start;
        
        @Schema(description = "종료일")
        private String end;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Schema(description = "담당자 정보")
    public static class ManagersDTO {
        @Schema(description = "담당자 수")
        private int cnt;
        
        @Schema(description = "담당자 상세 정보")
        private List<ManagerInfoDTO> info;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Schema(description = "담당자 상세 정보")
    public static class ManagerInfoDTO {
        @Schema(description = "프로필 URL")
        private String profileUrl;
        
        @Schema(description = "담당자 이름")
        private String name;
    }
}