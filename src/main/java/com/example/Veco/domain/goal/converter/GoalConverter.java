package com.example.Veco.domain.goal.converter;

import com.example.Veco.domain.goal.dto.request.GoalReqDTO;
import com.example.Veco.domain.goal.dto.response.GoalResDTO;
import com.example.Veco.domain.goal.entity.Goal;
import com.example.Veco.domain.mapping.MemberTeam;
import com.example.Veco.domain.team.entity.Team;

import java.time.LocalDateTime;
import java.util.List;

public class GoalConverter {

    // 목표 생성: dto, Team, name -> Goal
    public static Goal toGoal (
            GoalReqDTO.CreateGoal dto,
            Team team,
            String name
    ){
        return Goal.builder()
                .state(dto.state())
                .content(dto.content())
                .title(dto.title())
                .deadlineStart(dto.deadline().start())
                .deadlineEnd(dto.deadline().end())
                .priority(dto.priority())
                .team(team)
                .name(name)
                .build();
    }

    // goalId, Time -> CreateGoal
    public static GoalResDTO.CreateGoal toCreateGoal(
            Long goalId,
            LocalDateTime now
    ){
        return GoalResDTO.CreateGoal.builder()
                .goalId(goalId)
                .createdAt(now)
                .build();
    }

    // goalId, Time -> UpdateGoal
    public static GoalResDTO.UpdateGoal toUpdateGoal(
            Long goalId,
            LocalDateTime now
    ){
        return GoalResDTO.UpdateGoal.builder()
                .goalId(goalId)
                .updatedAt(now)
                .build();
    }

    // Data -> DataDTO
    public static <T> GoalResDTO.Data<T> toData (
            List<T> info
    ){
        return GoalResDTO.Data.<T>builder()
                .cnt(info.size())
                .info(info)
                .build();
    }

    // MemberTeam -> Teammate
    public static GoalResDTO.Teammate toTeammate (
            MemberTeam memberTeam
    ){
        return GoalResDTO.Teammate.builder()
                .id(memberTeam.getMember().getId())
                .nickname(memberTeam.getMember().getNickname())
                .build();
    }
}
