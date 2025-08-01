package com.example.Veco.domain.issue.service.command;

import com.example.Veco.domain.assignee.converter.AssigneeConverter;
import com.example.Veco.domain.assignee.repository.AssigneeRepository;
import com.example.Veco.domain.goal.entity.Goal;
import com.example.Veco.domain.goal.exception.GoalException;
import com.example.Veco.domain.goal.exception.code.GoalErrorCode;
import com.example.Veco.domain.goal.repository.GoalRepository;
import com.example.Veco.domain.issue.converter.IssueConverter;
import com.example.Veco.domain.issue.dto.IssueReqDTO;
import com.example.Veco.domain.issue.dto.IssueResponseDTO;
import com.example.Veco.domain.issue.entity.Issue;
import com.example.Veco.domain.issue.exception.IssueException;
import com.example.Veco.domain.issue.exception.code.IssueErrorCode;
import com.example.Veco.domain.issue.repository.IssueRepository;
import com.example.Veco.domain.mapping.entity.MemberTeam;
import com.example.Veco.domain.mapping.repository.MemberTeamRepository;
import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.member.error.MemberErrorStatus;
import com.example.Veco.domain.member.error.MemberHandler;
import com.example.Veco.domain.member.repository.MemberRepository;
import com.example.Veco.global.auth.user.AuthUser;
import com.example.Veco.global.enums.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class IssueCommandServiceImpl implements IssueCommandService {

    private final MemberRepository memberRepository;
    private final MemberTeamRepository memberTeamRepository;
    private final IssueRepository issueRepository;
    private final AssigneeRepository assigneeRepository;
    private final GoalRepository goalRepository;

    @Override
    @Transactional
    public IssueResponseDTO.UpdateIssue updateIssue(AuthUser user, IssueReqDTO.UpdateIssue dto, Long teamId, Long issueId
    ){
        Member member = memberRepository.findBySocialUid(user.getSocialUid())
                .orElseThrow(() -> new MemberHandler(MemberErrorStatus._MEMBER_NOT_FOUND));
        memberTeamRepository.findByMemberIdAndTeamId(member.getId(), teamId)
                .orElseThrow(() -> new MemberHandler(MemberErrorStatus._FORBIDDEN));
        Issue issue = issueRepository.findById(issueId)
                .orElseThrow(() -> new IssueException(IssueErrorCode.NOT_FOUND));
        if (!issue.getTeam().getId().equals(teamId)) {
            throw new IssueException(IssueErrorCode.FORBIDDEN);
        }

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
        if (!isRestore) {
            return null;
        } else {
            LocalDateTime now = LocalDateTime.now();
            return IssueConverter.toUpdateIssue(issueId, now);
        }
    }

    @Override
    @Transactional
    public List<Long> deleteIssue(AuthUser user, Long teamId, IssueReqDTO.DeleteIssue dto){
        List<Issue> issues = issueRepository.findAllById(dto.issueIds());

        Member member = memberRepository.findBySocialUid(user.getSocialUid())
                .orElseThrow(() -> new MemberHandler(MemberErrorStatus._MEMBER_NOT_FOUND));

        memberTeamRepository.findByMemberIdAndTeamId(member.getId(), teamId)
                .orElseThrow(() -> new MemberHandler(MemberErrorStatus._FORBIDDEN));

        List<Long> result = new ArrayList<>();
        issues = issues.stream()
                .filter(issue -> issue.getTeam().getId().equals(teamId))
                .toList();

        issues.forEach(issue -> result.add(issue.getId()));

        issueRepository.deleteAll(issues);
        assigneeRepository.deleteAllByTypeAndTargetIds(Category.ISSUE, result);

        return result;
    }
}
