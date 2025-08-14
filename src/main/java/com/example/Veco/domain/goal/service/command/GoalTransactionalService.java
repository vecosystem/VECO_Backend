package com.example.Veco.domain.goal.service.command;

import com.example.Veco.domain.assignee.entity.Assignee;
import com.example.Veco.domain.assignee.converter.AssigneeConverter;
import com.example.Veco.domain.assignee.repository.AssigneeRepository;
import com.example.Veco.domain.goal.converter.GoalConverter;
import com.example.Veco.domain.goal.dto.request.GoalReqDTO;
import com.example.Veco.domain.goal.entity.Goal;
import com.example.Veco.domain.goal.exception.GoalException;
import com.example.Veco.domain.goal.exception.code.GoalErrorCode;
import com.example.Veco.domain.goal.repository.GoalRepository;
import com.example.Veco.domain.issue.entity.Issue;
import com.example.Veco.domain.issue.exception.IssueException;
import com.example.Veco.domain.issue.exception.code.IssueErrorCode;
import com.example.Veco.domain.issue.repository.IssueRepository;
import com.example.Veco.domain.mapping.entity.MemberTeam;
import com.example.Veco.domain.mapping.repository.MemberTeamRepository;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.domain.team.exception.TeamException;
import com.example.Veco.domain.team.exception.code.TeamErrorCode;
import com.example.Veco.domain.team.repository.TeamRepository;
import com.example.Veco.global.enums.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class GoalTransactionalService {

    // 리포지토리
    private final TeamRepository teamRepository;
    private final GoalRepository goalRepository;
    private final AssigneeRepository assigneeRepository;
    private final IssueRepository issueRepository;
    private final MemberTeamRepository memberTeamRepository;

    // 목표 이름(Veco-g3) 조회, 생성
    @Transactional
    protected Long createGoal(
            Long teamId,
            GoalReqDTO.CreateGoal dto,
            List<MemberTeam> memberTeamList,
            List<Issue> issueList
    ){
        // Team 조회
        Team team = teamRepository.findTeamById(teamId).orElseThrow(() ->
                new TeamException(TeamErrorCode._NOT_FOUND));

        // 목표 생성
        String name = team.getWorkSpace().getName()+"-g"+team.getGoalNumber();
        Goal goal = goalRepository.save(GoalConverter.toGoal(dto,team,name));

        // 목표 <-> 담당자 연결: 없으면 null로 저장
        List<Assignee> assigneeList = new ArrayList<>();
        memberTeamList.forEach(
                value -> assigneeList.add(
                        AssigneeConverter.toAssignee(
                                value, Category.GOAL, goal
                        )
                )
        );
        assigneeRepository.saveAll(assigneeList);

        if (assigneeList.isEmpty()){
            assigneeRepository.save(AssigneeConverter.toAssignee(null, Category.GOAL, goal));
        }

        // 목표 <-> 이슈 연결
        issueList.forEach(
                value -> value.updateGoal(goal)
        );

        // 목표이름 +1 (Veco-g3 -> Veco-g4)
        team.updateGoalNumber(team.getGoalNumber()+1);

        return goal.getId();
    }

    // 목표 수정
    @Transactional
    protected boolean updateGoal(
            GoalReqDTO.UpdateGoal dto,
            Long goalId,
            Long teamId
    ){
        Goal goal = goalRepository.findById(goalId).orElseThrow(() ->
                new GoalException(GoalErrorCode.NOT_FOUND));

        boolean isRestore = false;
        // title 변경
        if (dto.title() != null) {
            goal.updateTitle(dto.title());
            isRestore = true;
        }
        // content 변경
        if (dto.content() != null) {
            goal.updateContent(dto.content());
            isRestore = true;
        }
        // state 변경
        if (dto.state() != null) {
            goal.updateState(dto.state());
            isRestore = true;
        }
        // priority 변경
        if (dto.priority() != null) {
            goal.updatePriority(dto.priority());
            isRestore = true;
        }
        // 담당자 변경
        if (dto.managersId() != null) {

            // 신규 담당자 존재여부 확인, 조회
            List<MemberTeam> memberTeamList = memberTeamRepository
                    .findAllByMemberIdInAndTeamId(dto.managersId(), teamId);

            // 기존 담당자 삭제
            assigneeRepository.deleteAllByTypeAndTargetId(Category.GOAL, goalId);

            // 신규 담당자 추가
            memberTeamList.forEach(
                    value -> assigneeRepository.save(
                            AssigneeConverter.toAssignee(value, Category.GOAL, goal)
                    )
            );

            // 신규 담당자가 없는 경우, null로
            if (memberTeamList.isEmpty()){
                assigneeRepository.save(AssigneeConverter.toAssignee(null, Category.GOAL, goal));
            }

            isRestore = true;
        }
        // 기한 변경
        if (dto.deadline() != null) {

            try {
                if (dto.deadline().start() != null) {
                    LocalDate start;
                    if (dto.deadline().start().equals("null")){
                        start = null;
                    } else {
                        start = LocalDate.parse(dto.deadline().start());
                    }
                    goal.updateDeadlineStart(start);
                }
                if (dto.deadline().end() != null) {
                    LocalDate end;
                    if (dto.deadline().end().equals("null")){
                        end = null;
                    } else {
                        end = LocalDate.parse(dto.deadline().end());
                    }
                    goal.updateDeadlineEnd(end);
                }
                isRestore = true;
            } catch (DateTimeParseException e) {
                throw new GoalException(GoalErrorCode.DEADLINE_INVALID);
            }

        }
        // 이슈 변경
        if (dto.issuesId() != null){

            // 새로운 이슈 존재 여부 검증
            List<Issue> issueList = issueRepository.findAllById(dto.issuesId());
            if (issueList.size() != dto.issuesId().size()){
                throw new IssueException(IssueErrorCode.NOT_FOUND);
            }

            // 기존 이슈 조회, 목표 해제
            List<Issue> oldIssueList = issueRepository.findAllByGoal(goal).orElse(new ArrayList<>());
            oldIssueList.forEach(
                    value -> value.updateGoal(null)
            );

            // 이슈 목표 변경
            issueList.forEach(
                    value -> value.updateGoal(goal)
            );

            isRestore = true;
        }
        return isRestore;
    }
}
