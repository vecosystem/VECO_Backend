package com.example.Veco.domain.external.dto.request;

import com.example.Veco.global.enums.ExtServiceType;
import com.example.Veco.global.enums.Priority;
import com.example.Veco.global.enums.State;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

public class ExternalRequestDTO {

    @Getter
    public static class ExternalCreateRequestDTO{
        private String owner;
        private String repo;
        private Long installationId;
        @NotBlank(message = "제목은 필수입니다.")
        private String title;
        private String content;
        private State state;
        private Priority priority;
        private List<Long> managersId;
        private DeadlineRequestDTO deadline;
        @NotNull(message = "외부는 반드시 설정해야합니다.")
        private ExtServiceType extServiceType;
        private Long goalId;
    }

    @Getter
    public static class DeadlineRequestDTO{
        private String start;
        private String end;

        public Optional<LocalDate> getParsedStartDate() {
            return parseDate(start);
        }

        public Optional<LocalDate> getParsedEndDate() {
            return parseDate(end);
        }

        private Optional<LocalDate> parseDate(String dateStr) {
            if (dateStr == null) return Optional.empty(); // 필드 생략 = 변경하지 않음
            if ("null".equalsIgnoreCase(dateStr)) return Optional.of(null); // 명시적 삭제

            try {
                return Optional.of(LocalDate.parse(dateStr));
            } catch (DateTimeParseException e) {
                throw new IllegalArgumentException("Invalid date format: " + dateStr);
            }
        }

        public boolean shouldUpdateStartDate() {
            return start != null;
        }

        public boolean shouldUpdateEndDate() {
            return end != null;
        }
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
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ExternalGroupedSearchRequestDTO {
        @NotNull(message = "필터 타입은 필수입니다.")
        private FilterType filterType;
        private String cursor;
        @Builder.Default
        private Integer size = 50;

        public enum FilterType {
            ASSIGNEE, GOAL, PRIORITY, STATE, EXT_TYPE
        }
    }
}
