package com.example.Veco.domain.assignee.repository;

import com.example.Veco.domain.assignee.entity.Assignee;
import com.example.Veco.domain.assignee.entity.QAssignee;
import com.example.Veco.domain.mapping.entity.MemberTeam;
import com.example.Veco.domain.mapping.entity.QMemberTeam;
import com.example.Veco.domain.member.entity.QMember;
import com.example.Veco.domain.profile.entity.QProfile;
import com.example.Veco.global.enums.Category;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AssigneeQueryDslImpl implements AssigneeQueryDsl {
    private final JPAQueryFactory queryFactory;
    private final QAssignee assignee = QAssignee.assignee;

    @Override
    public List<Assignee> findByMemberTeamsAndTypeAndTargetIds(List<MemberTeam> memberTeams, Category type, List<Long> targetIds) {
        QAssignee assignee = QAssignee.assignee;

        return queryFactory
                .selectFrom(assignee)
                .where(
                        assignee.memberTeam.in(memberTeams),
                        assignee.type.eq(type),
                        assignee.targetId.in(targetIds)
                )
                .fetch();
    }

    @Override
    public List<Assignee> findByIssueIdIn(List<Long> issueIds) {
        QAssignee assignee = QAssignee.assignee;
        QMemberTeam memberTeam = QMemberTeam.memberTeam;
        QMember member = QMember.member;
        QProfile profile = QProfile.profile;

        return queryFactory.selectFrom(assignee)
                .leftJoin(assignee.memberTeam, memberTeam).fetchJoin()
                .leftJoin(memberTeam.member, member).fetchJoin()
                .leftJoin(member.profile, profile).fetchJoin()
                .where(assignee.issue.id.in(issueIds))
                .fetch();
    }

    @Override
    public List<Assignee> findByGoalIdIn(List<Long> goalIds) {
        QAssignee assignee = QAssignee.assignee;
        QMemberTeam memberTeam = QMemberTeam.memberTeam;
        QMember member = QMember.member;
        QProfile profile = QProfile.profile;

        return queryFactory.selectFrom(assignee)
                .leftJoin(assignee.memberTeam, memberTeam).fetchJoin()
                .leftJoin(memberTeam.member, member).fetchJoin()
                .leftJoin(member.profile, profile).fetchJoin()
                .where(assignee.goal.id.in(goalIds))
                .fetch();
    }

    @Override
    public List<Assignee> findByExternalIdIn(List<Long> externalIds) {
        QAssignee assignee = QAssignee.assignee;
        QMemberTeam memberTeam = QMemberTeam.memberTeam;
        QMember member = QMember.member;
        QProfile profile = QProfile.profile;

        return queryFactory.selectFrom(assignee)
                .leftJoin(assignee.memberTeam, memberTeam).fetchJoin()
                .leftJoin(memberTeam.member, member).fetchJoin()
                .leftJoin(member.profile, profile).fetchJoin()
                .where(assignee.external.id.in(externalIds))
                .fetch();
    }

    @Override
    @Transactional
    public void deleteAllByTypeAndTargetIds(Category type, List<Long> targetIds) {
        queryFactory.delete(assignee)
                .where(
                        assignee.type.eq(type),
                        assignee.targetId.in(targetIds)
                )
                .execute();
    }

    @Override
    public List<Assignee> findByTypeAndTargetId(Category type, Long issueId) {
        QAssignee assignee = QAssignee.assignee;
        QMember member = QMember.member;

        return queryFactory.selectFrom(assignee)
                .innerJoin(member).on(member.id.eq(assignee.memberTeam.member.id))
                .where(
                        assignee.type.eq(type),
                        assignee.targetId.eq(issueId),
                        member.deletedAt.isNull()
                )
                .fetch();
    }
}
