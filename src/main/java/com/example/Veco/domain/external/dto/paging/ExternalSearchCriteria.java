package com.example.Veco.domain.external.dto.paging;

import com.example.Veco.domain.external.dto.request.ExternalRequestDTO;
import com.example.Veco.global.enums.ExtServiceType;
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
    private ExtServiceType extServiceType;
    private Long goalId;
    private Long teamId;
    private ExternalRequestDTO.ExternalGroupedSearchRequestDTO.FilterType filterType;

    public FilterType getActiveFilterType() {
        if(filterType != null) {
            return switch (filterType) {
                case STATE -> FilterType.STATE;
                case PRIORITY -> FilterType.PRIORITY;
                case ASSIGNEE -> FilterType.ASSIGNEE;
                case EXT_TYPE -> FilterType.EXT_TYPE;
                case GOAL -> FilterType.GOAL;
            };
        }
        
        if(state != null) return FilterType.STATE;
        if(priority != null) return FilterType.PRIORITY;
        if(assigneeId != null) return FilterType.ASSIGNEE;
        if(extServiceType != null) return FilterType.EXT_TYPE;
        if(goalId != null) return FilterType.GOAL;
        return FilterType.STATE;
    }

    public enum FilterType{
        STATE, PRIORITY, ASSIGNEE, EXT_TYPE, GOAL, NONE
    }
}
