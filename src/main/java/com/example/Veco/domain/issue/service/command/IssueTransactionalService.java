package com.example.Veco.domain.issue.service.command;

import com.example.Veco.domain.assignee.converter.AssigneeConverter;
import com.example.Veco.domain.assignee.repository.AssigneeRepository;
import com.example.Veco.domain.goal.entity.Goal;
import com.example.Veco.domain.goal.exception.GoalException;
import com.example.Veco.domain.goal.exception.code.GoalErrorCode;
import com.example.Veco.domain.goal.repository.GoalRepository;
import com.example.Veco.domain.issue.dto.IssueReqDTO;
import com.example.Veco.domain.issue.entity.Issue;
import com.example.Veco.domain.issue.exception.IssueException;
import com.example.Veco.domain.issue.exception.code.IssueErrorCode;
import com.example.Veco.domain.issue.repository.IssueRepository;
import com.example.Veco.domain.mapping.entity.MemberTeam;
import com.example.Veco.domain.mapping.repository.MemberTeamRepository;
import com.example.Veco.global.enums.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
public class IssueTransactionalService {

    private final IssueRepository issueRepository;
    private final GoalRepository goalRepository;
    private final AssigneeRepository assigneeRepository;
    private final MemberTeamRepository memberTeamRepository;

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
            issue.updateDeadlineStart(dto.deadline().start());
            issue.updateDeadlineEnd(dto.deadline().end());
            isRestore = true;
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
