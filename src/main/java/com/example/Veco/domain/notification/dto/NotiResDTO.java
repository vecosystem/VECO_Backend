package com.example.Veco.domain.notification.dto;

import com.example.Veco.global.enums.Category;
import com.example.Veco.global.enums.Priority;
import com.example.Veco.global.enums.State;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

public class NotiResDTO {

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GroupedNotiList<T> {
        private Category type;
        private LocalDate deadline;
        private List<NotiGroup<T>> groupedList;
        private Integer totalSize;

        @Getter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class NotiGroup<T> {
            private String groupTitle;  // 상태명, 우선순위명
            private List<T> notiList;
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IssuePreViewDTO {
        private Long alarmId;  //memberNotificationId
        private String name;
        private Long typeId;
        private String title;
        private State state;
        private Priority priority;
        private String goalTitle;
        private boolean isRead;
    }

    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GoalPreViewDTO {
        private Long alarmId; //memberNotificationId
        private String name;
        private Long typeId;
        private String title;
        private State state;
        private Priority priority;
        private boolean isRead;
    }

    // 외부 DTO

}
