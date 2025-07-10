package com.example.Veco.domain.goal.dto.request;

import com.example.Veco.global.enums.Priority;
import com.example.Veco.global.enums.State;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;
import java.util.List;

public class GoalReqDTO {

    // 목표 작성
    public record CreateGoal (
            String title,
            String content,
            State state,
            Priority priority,
            List<Long> managersId,
            Boolean isIncludeMe,
            Deadline deadline,
            List<Long> issueId
    ){}

    // 목표 수정
    public record UpdateGoal(
            String title,
            String content,
            State state,
            Priority priority,
            List<Long> managersId,
            Deadline deadline,
            List<Long> issuesId
    ){}

    // 세부 속성들
    // 기한
    public record Deadline (
            LocalDate start,
            LocalDate end
    ){}
}
