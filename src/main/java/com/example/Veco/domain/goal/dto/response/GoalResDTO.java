package com.example.Veco.domain.goal.dto.response;

import com.example.Veco.global.enums.Priority;
import com.example.Veco.global.enums.State;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class GoalResDTO {

    // 커서 기반 페이지네이션 틀
    @Builder
    public record Pageable<T> (
            List<T> data,
            Boolean hasNext,
            String nextCursor,
            Integer pageSize
    ){}

    // 필터 적용 틀: 팀 내 모든 목표 조회
    @Builder
    public record FilteringGoal<T> (
            String filterName,
            Integer dataCnt,
            List<T> goals
    ){}

    // 간단 조회: 팀 내 모든 목표 조회
    @Builder
    public record SimpleGoal (
            Long id,
            String name,
            String title,
            State state,
            Priority priority,
            Deadline deadline,
            QData managers
    ){}

    // 자세한 조회: 목표 상세 조회
    @Builder
    public record FullGoal (
            String name,
            String title,
            String content,
            String priority,
            Data<ManagerInfo> managers,
            Deadline deadline,
            Data<IssueInfo> issues,
            Data<CommentInfo> comments
    ){}

    // 팀원 조회: 변경 가능성 O
    @Builder
    public record Teammate (
            Long id,
            String nickname
    ){}

    // 목표 작성: 변경 가능성 O
    @Builder
    public record CreateGoal (
            Long goalId,
            LocalDateTime createdAt
    ){}

    // 목표 수정
    @Builder
    public record UpdateGoal(
            Long goalId,
            LocalDateTime updatedAt
    ){}

    // 세부 속성들
    // 기한
    @Builder
    public record Deadline (
            LocalDate start,
            LocalDate end
    ){}

    // 데이터 틀: 이슈 정보, 댓글 정보, 담당자 정보
    @Builder
    public record Data<T> (
            Integer cnt,
            List<T> info
    ){}

    // QueryDSL용 담당자 정보
    @Builder
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class QData {
        private Long cnt;
        private List<ManagerInfo> info;

    }


    // 이슈 정보
    @Builder
    public record IssueInfo (
            Long id,
            String title
    ){}

    // 댓글 정보
    @Builder
    public record CommentInfo (
            String profileUrl,
            String nickname,
            LocalDateTime createdAt,
            String content
    ){}

    // 담당자 정보
    @Builder
    public record ManagerInfo(
            String profileUrl,
            String name
    ){}

    // 목표 정보
    @Builder
    public record GoalInfo (
            Long id,
            String title
    ){}
}
