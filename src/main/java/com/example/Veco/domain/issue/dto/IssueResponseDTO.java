package com.example.Veco.domain.issue.dto;

import com.example.Veco.domain.comment.entity.Comment;
import com.example.Veco.global.enums.Priority;
import com.example.Veco.global.enums.State;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class IssueResponseDTO {

    // DB 조회용 DTO
    @Builder
    public record SimpleIssue(
            Long id,
            String name,
            String title,
            State state,
            Priority priority,
            Deadline deadline,
            GoalInfo goal
    ) {
    }

    // 데이터 틀: 이슈 정보, 댓글 정보, 담당자 정보
    @Builder
    public record Data<T> (
            Integer cnt,
            List<T> info
    ){}

    @Builder
    public record Deadline(
            LocalDate start,
            LocalDate end
    ) {
    }

    @Builder
    public record ManagerInfo(
            Long id,
            Long issueId,
            String profileImage,
            String name
    ) {
    }

    @Builder
    public record SimpleManagerInfo(
            String profileUrl,
            String name
    ) {
    }

    @Builder
    public record GoalInfo(
            Long id,
            String title
    ) {
    }

    // 필터 적용 틀: 팀 내 모든 이슈 조회
    @Builder
    public record FilteringIssue<T>(
            String filterName,
            Integer dataCnt,
            List<T> issues
    ) {
    }

    // 이슈 + 담당자 정보 응답용 DTO
    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class IssueWithManagers {
        Long id;
        String name;
        String title;
        State state;
        Priority priority;
        Deadline deadline;
        GoalInfo goal;
        List<ManagerInfo> managers;
    }

    // 커서 기반 페이지네이션 틀
    @Builder
    public record Pageable<T>(
            List<T> data,
            Boolean hasNext,
            String nextCursor,
            Integer pageSize
    ) {
    }

    @Builder
    public record DetailIssue(
            Long id,
            String name,
            String title,
            String content,
            State state,
            Priority priority,
            Deadline deadline,
            GoalInfo goal,
            Data<SimpleManagerInfo> managers,
            Data<CommentInfo> comments
    ) {
    }

    @Builder
    public record CommentInfo(
            String name,
            String profileUrl,
            String content,
            LocalDateTime createdAt
    ) {
    }

}
