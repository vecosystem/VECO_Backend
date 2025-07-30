package com.example.Veco.domain.external.dto.request;

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
        private String owner;
        private String repo;
        private Long installationId;
        private String title;
        private String content;
        private State state;
        private Priority priority;
        private List<Long> managersId;
        private DeadlineRequestDTO deadline;
        private ExtServiceType extServiceType;
        private Long goalId;
    }

    @Getter
    public static class DeadlineRequestDTO{
        private LocalDate start;
        private LocalDate end;
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
        private String content;
        private State state;
        private Priority priority;
        private List<Long> managersId;
        private DeadlineRequestDTO deadline;
        private Long goalId;
        private ExtServiceType extServiceType;
    }
}
