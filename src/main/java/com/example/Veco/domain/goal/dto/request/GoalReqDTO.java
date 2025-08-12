package com.example.Veco.domain.goal.dto.request;

import com.example.Veco.global.enums.Priority;
import com.example.Veco.global.enums.State;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

public class GoalReqDTO {

    // 목표 작성
    public record CreateGoal (
            @Size(max = 20, message = "최대 20자까지 작성할 수 있습니다.")
            @NotBlank(message = "제목은 반드시 작성해야 합니다.")
            String title,
            String content,
            State state,
            Priority priority,
            List<Long> managersId,
            Deadline deadline,
            List<Long> issuesId
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

    // 목표 삭제, 목표 복원
    public record DeleteGoal(
            List<Long> goalIds
    ){}

    // 세부 속성들
    // 기한
    public record Deadline (
            String start,
            String end
    ){}
}
