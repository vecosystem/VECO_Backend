package com.example.Veco.domain.issue.repository;

import com.example.Veco.domain.assignee.entity.QAssignee;
import com.example.Veco.domain.goal.entity.QGoal;
import com.example.Veco.domain.issue.dto.IssueResponseDTO;
import com.example.Veco.domain.issue.entity.Issue;
import com.example.Veco.domain.issue.entity.QIssue;
import com.example.Veco.domain.issue.exception.IssueException;
import com.example.Veco.domain.issue.exception.code.IssueErrorCode;
import com.example.Veco.domain.member.entity.QMember;
import com.example.Veco.global.enums.Category;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class CustomIssueRepositoryImpl implements CustomIssueRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<IssueResponseDTO.SimpleIssue> findIssuesByTeamId(Predicate query, int size) {
        QIssue issue = QIssue.issue;
        QGoal goal = QGoal.goal;
        QAssignee assignee = QAssignee.assignee;
        QMember member = QMember.member;

        List<IssueResponseDTO.SimpleIssue> result = queryFactory
                .from(issue)
                .where(query)
                .leftJoin(assignee).on(assignee.type.eq(Category.ISSUE).and(assignee.targetId.eq(issue.id)))
                .leftJoin(member).on(member.eq(assignee.memberTeam.member))
                .leftJoin(goal).on(goal.id.eq(issue.goal.id))
                .orderBy(issue.id.desc())
                .groupBy(issue.id, assignee.id)
                .transform(GroupBy.groupBy(issue.id).list(
                                Projections.constructor(
                                        IssueResponseDTO.SimpleIssue.class,
                                        issue.id,
                                        issue.name,
                                        issue.title,
                                        issue.state,
                                        issue.priority,
                                        Projections.constructor(
                                                IssueResponseDTO.Deadline.class,
                                                issue.deadlineStart,
                                                issue.deadlineEnd
                                        ),
                                        Projections.constructor(
                                                IssueResponseDTO.GoalInfo.class,
                                                goal.id.coalesce(-1L),
                                                goal.title.coalesce("목표 없음")
                                        )

                                )
                        )
                );

        // 이슈가 없는 경우 throw
        if (result.isEmpty()){
            throw new IssueException(IssueErrorCode.NOT_FOUND_IN_TEAM);
        }

        return result;
    }

    // 필터에 맞는 모든 목표 개수 조회
    @Override
    public Long findIssuesCountByFilter(
            Predicate query
    ) {
        // 객체 생성
        QIssue issue = QIssue.issue;

        return queryFactory
                .select(issue.countDistinct())
                .from(issue)
                .where(query)
                .fetchFirst();
    }

    // 모든 목표 담당자 리스트 조회
    @Override
    public List<String> findIssuesAssigneeInTeam(
            Long teamId
    ) {
        // 객체 생성
        QIssue issue = QIssue.issue;
        QAssignee assignee = QAssignee.assignee;

        return queryFactory
                .select(assignee.memberTeam.member.name)
                .from(issue)
                .leftJoin(assignee).on(assignee.type.eq(Category.ISSUE)
                        .and(assignee.targetId.eq(issue.id)))
                .where(issue.goal.team.id.eq(teamId))
                .fetch();
    }

    @Override
    public List<IssueResponseDTO.GoalInfo> findGoalInfoByTeamId(
            Long teamId
    ) {
        // 객체 생성
        QIssue issue = QIssue.issue;
        QGoal goal = QGoal.goal;

        return queryFactory
                .select(Projections.constructor(
                        IssueResponseDTO.GoalInfo.class,
                        goal.id.coalesce(-1L),
                        goal.title.coalesce("목표 없음")
                ))
                .from(issue)
                .leftJoin(goal)
                .on(goal.id.eq(issue.goal.id)
                        .and(goal.deletedAt.isNull()))
                .where(issue.team.id.eq(teamId)
                        .or(goal.id.isNull()))
                .fetch();
    }

    @Override
    public Map<Long, List<IssueResponseDTO.ManagerInfo>> findManagerInfoByTeamId(
            Long teamId
    ) {
        // 객체 생성
        QIssue issue = QIssue.issue;
        QAssignee assignee = QAssignee.assignee;
        QMember member = QMember.member;

        Map<Long, List<IssueResponseDTO.ManagerInfo>> result = queryFactory
                .from(issue)
                .leftJoin(assignee).on(assignee.type.eq(Category.ISSUE)
                        .and(assignee.targetId.eq(issue.id)))
                .leftJoin(member).on(member.eq(assignee.memberTeam.member))
                .transform(
                        GroupBy.groupBy(issue.id).as(
                                GroupBy.list(
                                        Projections.constructor(
                                                IssueResponseDTO.ManagerInfo.class,
                                                member.id,
                                                issue.id,
                                                member.profile.profileImageUrl,
                                                member.name
                                        )
                                )
                        )
                );

        return result;

    }
}
