package com.example.Veco.domain.goal.service.query;

import com.example.Veco.domain.assignee.entity.Assignee;
import com.example.Veco.domain.assignee.entity.AssigneeRepository;
import com.example.Veco.domain.comment.entity.Comment;
import com.example.Veco.domain.comment.entity.CommentRepository;
import com.example.Veco.domain.comment.entity.CommentRoom;
import com.example.Veco.domain.comment.entity.CommentRoomRepository;
import com.example.Veco.domain.goal.converter.GoalConverter;
import com.example.Veco.domain.goal.dto.response.GoalResDTO;
import com.example.Veco.domain.goal.dto.response.GoalResDTO.Data;
import com.example.Veco.domain.goal.dto.response.GoalResDTO.FilteringGoal;
import com.example.Veco.domain.goal.dto.response.GoalResDTO.FullGoal;
import com.example.Veco.domain.goal.dto.response.GoalResDTO.Pageable;
import com.example.Veco.domain.goal.dto.response.GoalResDTO.SimpleGoal;
import com.example.Veco.domain.goal.dto.response.GoalResDTO.Teammate;
import com.example.Veco.domain.goal.entity.Goal;
import com.example.Veco.domain.goal.exception.GoalException;
import com.example.Veco.domain.goal.exception.code.GoalErrorCode;
import com.example.Veco.domain.goal.repository.GoalRepository;
import com.example.Veco.domain.issue.entity.Issue;
import com.example.Veco.domain.issue.entity.IssueRepository;
import com.example.Veco.domain.mapping.MemberTeam;
import com.example.Veco.domain.mapping.MemberTeamRepository;
import com.example.Veco.global.apiPayload.ApiResponse;
import com.example.Veco.global.enums.Category;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoalQueryService {

    // 리포지토리
    private final GoalRepository goalRepository;
    private final MemberTeamRepository memberTeamRepository;
    private final AssigneeRepository assigneeRepository;
    private final IssueRepository issueRepository;
    private final CommentRepository commentRepository;
    private final CommentRoomRepository commentRoomRepository;

    // 팀 내 모든 목표 조회
    public Pageable<FilteringGoal<SimpleGoal>> getGoals(
            Long teamId,
            String cursor,
            Integer size,
            String query
    ) {
        return null;
    }

    // 목표 간단 조회
    public Data<GoalResDTO.GoalInfo> getSimpleGoal(
            Long teamId
    ) {
        // 목표 조회
        List<Goal> goals = goalRepository.findAllByTeamId(teamId);
        if (goals.isEmpty()){
            throw new GoalException(GoalErrorCode.NOT_FOUND_IN_TEAM);
        }

        // DTO 변환
        return GoalConverter.toData(goals.stream().map(GoalConverter::toGoalInfo).toList());
    }

    // 목표 상세 조회
    public FullGoal getGoalDetail(
            Long goalId
    ){

        // 필요한 요소: 목표, 담당자, 이슈, 댓글
        // 목표 조회
        Goal goal = goalRepository.findById(goalId).orElseThrow(() ->
                new GoalException(GoalErrorCode.NOT_FOUND));

        // 담당자 조회: 없으면 []
        List<Assignee> assignees = assigneeRepository.findAllByTypeAndTargetId(Category.GOAL, goalId)
                .orElse(new ArrayList<>());

        // 이슈 조회: 없으면 []
        List<Issue> issues = issueRepository.findAllByGoal(goal)
                .orElse(new ArrayList<>());

        // 댓글 조회(댓글방 조회 -> 댓글 조회, 댓글 최신순): 없으면 []
        CommentRoom commentRooms = commentRoomRepository.findByRoomTypeAndTargetId(Category.GOAL, goalId);
        List<Comment> comments = commentRepository.findAllByCommentRoomOrderByIdDesc(commentRooms)
                .orElse(new ArrayList<>());

        // 조회한 요소들 DTO 변환
        return GoalConverter.toFullGoal(
                goal,
                issues.stream().map(GoalConverter::toIssueInfo).toList(),
                assignees.stream().map(GoalConverter::toManagerInfo).toList(),
                comments.stream().map(GoalConverter::toCommentInfo).toList()
        );
    }

    // 팀원 조회
    public Data<Teammate> getTeammate(
            Long teamId
    ){
        // 팀원 조회
        List<MemberTeam> memberTeam = memberTeamRepository.findAllByTeamId(teamId);

        // 존재하면 DTO 담아 반환
        if (!memberTeam.isEmpty()){
            // 조회한 팀원 DTO 변환
            List<Teammate> teammateList = memberTeam.stream()
                    .map(GoalConverter::toTeammate)
                    .toList();
            // 응답 DTO 변환
            return GoalConverter.toData(teammateList);
        } else {
            return null;
        }
    }
}
