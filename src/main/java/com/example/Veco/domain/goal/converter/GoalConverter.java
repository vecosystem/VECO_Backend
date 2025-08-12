package com.example.Veco.domain.goal.converter;

import com.example.Veco.domain.assignee.entity.Assignee;
import com.example.Veco.domain.comment.entity.Comment;
import com.example.Veco.domain.goal.dto.request.GoalReqDTO;
import com.example.Veco.domain.goal.dto.response.GoalResDTO;
import com.example.Veco.domain.goal.entity.Goal;
import com.example.Veco.domain.issue.entity.Issue;
import com.example.Veco.domain.mapping.entity.MemberTeam;
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

    // 팀 내 목표 모두 조회: FilteringData -> Pageable
    public static <T> GoalResDTO.Pageable<T> toPageable (
            List<T> data,
            boolean hasNext,
            String nextCursor,
            Integer pageSize
    ){
        return GoalResDTO.Pageable.<T>builder()
                .data(data)
                .hasNext(hasNext)
                .nextCursor(nextCursor)
                .pageSize(pageSize)
                .build();
    }

    // 목표 상세 조회: 목표, 담당자, 이슈, 댓글 -> DTO
    public static GoalResDTO.FullGoal toFullGoal (
            Goal goal,
            List<GoalResDTO.IssueInfo> issue,
            List<GoalResDTO.ManagerInfo> assignee,
            List<GoalResDTO.CommentInfo> comment
    ){
        return GoalResDTO.FullGoal.builder()
                .id(goal.getId())
                .name(goal.getName())
                .title(goal.getTitle())
                .content(goal.getContent())
                .state(goal.getState().name())
                .priority(goal.getPriority().name())
                .managers(toData(assignee))
                .deadline(toDeadline(goal))
                .issues(toData(issue))
                .comments(toData(comment))
                .build();
    }

    // 팀 내 목표 모두 조회: List<T>, filterName, dataCnt -> FilteringGoal
    public static <T> GoalResDTO.FilteringGoal<T> toFilteringGoal (
            List<T> simpleGoals,
            String filterName,
            Integer dataCnt
    ){
        return GoalResDTO.FilteringGoal.<T>builder()
                .filterName(filterName)
                .dataCnt(dataCnt)
                .goals(simpleGoals)
                .build();
    }

    // 목표 생성: goalId, Time -> CreateGoal
    public static GoalResDTO.CreateGoal toCreateGoal(
            Long goalId,
            LocalDateTime now
    ){
        return GoalResDTO.CreateGoal.builder()
                .goalId(goalId)
                .createdAt(now)
                .build();
    }

    // 목표 수정: goalId, Time -> UpdateGoal
    public static GoalResDTO.UpdateGoal toUpdateGoal(
            Long goalId,
            LocalDateTime now
    ){
        return GoalResDTO.UpdateGoal.builder()
                .goalId(goalId)
                .updatedAt(now)
                .build();
    }

    // goal -> Deadline
    public static GoalResDTO.Deadline toDeadline (
            Goal goal
    ){
        return GoalResDTO.Deadline.builder()
                .start(goal.getDeadlineStart())
                .end(goal.getDeadlineEnd())
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

    // Assignee -> ManagerInfo
    public static GoalResDTO.ManagerInfo toManagerInfo (
            Assignee assignee
    ){
        return GoalResDTO.ManagerInfo.builder()
                .name(assignee.getMemberTeam().getMember().getName())
                .profileUrl(assignee.getMemberTeam().getMember().getProfile().getProfileImageUrl())
                .build();
    }

    // Issue -> IssueInfo
    public static GoalResDTO.IssueInfo toIssueInfo (
            Issue issue
    ){
        return GoalResDTO.IssueInfo.builder()
                .id(issue.getId())
                .title(issue.getTitle())
                .build();
    }

    // Comment -> CommentInfo
    public static GoalResDTO.CommentInfo toCommentInfo (
            Comment comment
    ){
        return GoalResDTO.CommentInfo.builder()
                .profileUrl(comment.getMember().getProfile().getProfileImageUrl())
                .nickname(comment.getMember().getNickname())
                .createdAt(comment.getCreatedAt())
                .content(comment.getContent())
                .build();
    }

    // Goal -> GoalInfo
    public static GoalResDTO.GoalInfo toGoalInfo (
            Goal goal
    ){
        return GoalResDTO.GoalInfo.builder()
                .id(goal.getId())
                .title(goal.getTitle())
                .build();
    }
}
