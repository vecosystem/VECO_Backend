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

    // 이슈 DTO
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IssuePreViewListDTO {
        Category type;
        private LocalDate deadline;
        List<NotiResDTO.IssuePreViewDTO> notificationList;
        Integer listSize;
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

    // 목표 DTO
    @Builder
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GoalPreViewListDTO {
        Category type;
        private LocalDate deadline;
        List<NotiResDTO.GoalPreViewDTO> notificationList;
        Integer listSize;
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
