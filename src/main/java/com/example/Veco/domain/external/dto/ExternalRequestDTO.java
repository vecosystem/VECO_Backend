package com.example.Veco.domain.external.dto;

import com.example.Veco.global.enums.ExtServiceType;
import com.example.Veco.global.enums.Priority;
import com.example.Veco.global.enums.State;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class ExternalRequestDTO {

    @Getter
    public static class ExternalCreateRequestDTO{
        private String workSpaceName;
        private String title;
        private String description;
        private State state;
        private Priority priority;
        private List<Long> workers;
        private LocalDate startDate;
        private LocalDate endDate;
        private ExtServiceType extServiceType;
        private Long goalId;
    }

    @Getter
    public static class ExternalDeleteRequestDTO{
        private List<Long> externalIds;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ExternalUpdateRequestDTO{
        private String title;
        private String description;
        private State state;
        private Priority priority;
        private List<Long> assigneeIds;
        private LocalDate startDate;
        private LocalDate endDate;
        private Long goalId;
        private ExtServiceType extServiceType;
    }
}
