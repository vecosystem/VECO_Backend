package com.example.Veco.domain.issue.service.command;

import com.example.Veco.domain.assignee.converter.AssigneeConverter;
import com.example.Veco.domain.assignee.entity.Assignee;
import com.example.Veco.domain.assignee.repository.AssigneeRepository;
import com.example.Veco.domain.goal.entity.Goal;
import com.example.Veco.domain.goal.exception.GoalException;
import com.example.Veco.domain.goal.exception.code.GoalErrorCode;
import com.example.Veco.domain.goal.repository.GoalRepository;
import com.example.Veco.domain.issue.converter.IssueConverter;
import com.example.Veco.domain.issue.dto.IssueReqDTO;
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
public class IssueTransactionalService {

    private final IssueRepository issueRepository;
    private final GoalRepository goalRepository;
    private final AssigneeRepository assigneeRepository;
    private final MemberTeamRepository memberTeamRepository;
    private final TeamRepository teamRepository;

    @Transactional
    protected Long createIssue(
            Long teamId,
            IssueReqDTO.CreateIssue dto,
            List<MemberTeam> memberTeamList,
            Goal goal
    ){
        // Team 조회
        Team team = teamRepository.findTeamById(teamId).orElseThrow(() ->
                new TeamException(TeamErrorCode._NOT_FOUND));

        String name = team.getWorkSpace().getName()+"-i"+team.getIssueNumber();
        Issue issue = issueRepository.save(IssueConverter.toIssue(dto,team,name,goal));

        List<Assignee> assigneeList = new ArrayList<>();
        memberTeamList.forEach(
                value -> assigneeList.add(
                        AssigneeConverter.toIssueAssignee(
                                value, Category.ISSUE, issue
                        )
                )
        );
        assigneeRepository.saveAll(assigneeList);

        team.updateIssueNumber(team.getIssueNumber()+1);

        return issue.getId();
    }

    @Transactional
    protected boolean updateIssue(
            IssueReqDTO.UpdateIssue dto,
            Long issueId,
            Long teamId
    ){
        Issue issue = issueRepository.findById(issueId).orElseThrow(() ->
                new IssueException(IssueErrorCode.NOT_FOUND));

        boolean isRestore = false;
        if (dto.title() != null) {
            issue.updateTitle(dto.title());
            isRestore = true;
        }

        if (dto.content() != null) {
            issue.updateContent(dto.content());
            isRestore = true;
        }

        if (dto.state() != null) {
            issue.updateState(dto.state());
            isRestore = true;
        }

        if (dto.priority() != null) {
            issue.updatePriority(dto.priority());
            isRestore = true;
        }

        if (dto.managersId() != null) {

            assigneeRepository.deleteAllByTypeAndTargetId(Category.ISSUE, issueId);

            List<MemberTeam> memberTeamList = memberTeamRepository
                    .findAllByMemberIdInAndTeamId(dto.managersId(), teamId);
            memberTeamList.forEach(
                    value -> assigneeRepository.save(
                            AssigneeConverter.toIssueAssignee(value, Category.ISSUE, issue)
                    )
            );
            isRestore = true;
        }

        if (dto.deadline() != null) {

            try {
                if (dto.deadline().start() != null) {
                    LocalDate start;
                    if (dto.deadline().start().equals("null")){     // LocalDate -> String 변경
                        start = null;
                    } else {
                        start = LocalDate.parse(dto.deadline().start());
                    }
                    issue.updateDeadlineStart(start);
                }
                if (dto.deadline().end() != null) {
                    LocalDate end;
                    if (dto.deadline().end().equals("null")){
                        end = null;
                    } else {
                        end = LocalDate.parse(dto.deadline().end());
                    }
                    issue.updateDeadlineEnd(end);
                }
                isRestore = true;
            } catch (DateTimeParseException e) {
                throw new IssueException(IssueErrorCode.DEADLINE_INVALID);
            }
        }

        if (dto.goalId() != null) {
            Goal goal = goalRepository.findById(dto.goalId())
                    .orElseThrow(() -> new GoalException(GoalErrorCode.NOT_FOUND));
            issue.updateGoal(goal);
            isRestore = true;
        }
        return isRestore;
    }

}
