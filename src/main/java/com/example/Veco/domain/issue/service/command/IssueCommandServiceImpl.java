package com.example.Veco.domain.issue.service.command;

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
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.domain.team.exception.TeamException;
import com.example.Veco.domain.team.exception.code.TeamErrorCode;
import com.example.Veco.domain.team.repository.TeamRepository;
import com.example.Veco.global.auth.user.AuthUser;
import com.example.Veco.global.enums.Category;
import com.example.Veco.global.redis.exception.RedisException;
import com.example.Veco.global.redis.exception.code.RedisErrorCode;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class IssueCommandServiceImpl implements IssueCommandService {

    private final MemberRepository memberRepository;
    private final MemberTeamRepository memberTeamRepository;
    private final IssueRepository issueRepository;
    private final AssigneeRepository assigneeRepository;
    private final GoalRepository goalRepository;
    private final TeamRepository teamRepository;
    private final RedissonClient redissonClient;
    private final IssueTransactionalService issueTransactionalService;

    @Override
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

        boolean isRestore = issueTransactionalService.updateIssue(dto, issueId, teamId);
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
        if (issues.isEmpty() || issues.size() != dto.issueIds().size()){
            throw new IssueException(IssueErrorCode.NOT_FOUND);
        }

        Member member = memberRepository.findBySocialUid(user.getSocialUid())
                .orElseThrow(() -> new MemberHandler(MemberErrorStatus._MEMBER_NOT_FOUND));

        Team team = teamRepository.findById(teamId)
                        .orElseThrow(() -> new TeamException(TeamErrorCode._NOT_FOUND));

        memberTeamRepository.findByMemberIdAndTeamId(member.getId(), teamId)
                .orElseThrow(() -> new MemberHandler(MemberErrorStatus._FORBIDDEN));

        List<Long> result = new ArrayList<>();
        issues = issues.stream()
                .filter(issue -> issue.getTeam().getId().equals(teamId))
                .toList();

        issues.forEach(issue -> result.add(issue.getId()));

        issueRepository.deleteAll(issues);
        team.updateIssueNumber(team.getIssueNumber()-issues.size());

        return result;
    }

    @Override
    public IssueResponseDTO.CreateIssue createIssue(AuthUser user, Long teamId, IssueReqDTO.CreateIssue dto){

        List<Long> memberIds = new ArrayList<>(dto.managersId());
        if (dto.isIncludeMe()) {
            Member member = memberRepository.findBySocialUid(user.getSocialUid()).orElseThrow(() ->
                    new MemberHandler(MemberErrorStatus._MEMBER_NOT_FOUND));
            memberIds.add(member.getId());
        }

        List<Member> memberList = memberRepository.findAllById(memberIds);
        if (memberList.size() != memberIds.size()) {
            throw new MemberHandler(MemberErrorStatus._MEMBER_NOT_FOUND);
        }

        if (!teamRepository.existsById(teamId)) {
            throw new TeamException(TeamErrorCode._NOT_FOUND);
        }

        List<MemberTeam> memberTeamList = memberTeamRepository.findAllByMemberIdInAndTeamId(memberIds, teamId);
        if (memberTeamList.size() != memberIds.size()) {
            throw new MemberHandler(MemberErrorStatus._FORBIDDEN);
        }

        Goal goal = goalRepository.findById(dto.goalId()).orElseThrow(()->
                new GoalException(GoalErrorCode.NOT_FOUND));

        RLock lock = redissonClient.getLock("lock:issue:" + teamId);
        Long issueId;
        try {
            boolean available = lock.tryLock(60, 15, TimeUnit.SECONDS);
            if (!available) {
                throw new RedisException(RedisErrorCode.LOCK_TIMEOUT);
            }

            issueId = issueTransactionalService.createIssue(teamId, dto, memberTeamList, goal);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
        }
        LocalDateTime now = LocalDateTime.now();
        return IssueConverter.toCreateIssue(issueId, now);
    }
}
