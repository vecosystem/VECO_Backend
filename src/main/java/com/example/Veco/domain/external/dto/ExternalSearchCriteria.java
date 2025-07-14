package com.example.Veco.domain.external.dto;

import com.example.Veco.global.enums.Priority;
import com.example.Veco.global.enums.State;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExternalSearchCriteria {
    private State state;
    private Priority priority;
    private Long assigneeId;

    public FilterType getActiveFilterType() {
        if(state != null) return FilterType.STATE;
        if(priority != null) return FilterType.PRIORITY;
        if(assigneeId != null) return FilterType.ASSIGNEE;
        return FilterType.NONE;
    }

    public enum FilterType{
        STATE, PRIORITY, ASSIGNEE, NONE
    }
}
