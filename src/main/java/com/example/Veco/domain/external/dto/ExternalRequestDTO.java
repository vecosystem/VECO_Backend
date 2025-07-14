package com.example.Veco.domain.external.dto;

import com.example.Veco.global.enums.ExtServiceType;
import com.example.Veco.global.enums.Priority;
import com.example.Veco.global.enums.State;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

public class ExternalRequestDTO {

    @Getter
    public static class ExternalCreateRequestDTO{
        private String workSpaceName;
        private Long teamId;
        private String title;
        private String description;
        private State state;
        private Priority priority;
        private List<Long> workers;
        private LocalDate deadline;
        private ExtServiceType extServiceType;
    }

    @Getter
    public static class ExternalDeleteRequestDTO{
        private List<Long> externalIds;
    }

    @Getter
    public static class ExternalUpdateRequestDTO{
        private String title;
        private String description;
        private State state;
        private Priority priority;
        private List<Long> assigneeIds;
        private LocalDate deadline;
        private Long goalId;
        private ExtServiceType extServiceType;
    }
}
