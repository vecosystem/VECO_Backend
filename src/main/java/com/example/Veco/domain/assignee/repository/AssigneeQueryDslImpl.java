package com.example.Veco.domain.assignee.repository;

import com.example.Veco.domain.assignee.entity.Assignee;
import com.example.Veco.domain.assignee.entity.QAssignee;
import com.example.Veco.domain.mapping.MemberTeam;
import com.example.Veco.global.enums.Category;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AssigneeQueryDslImpl implements AssigneeQueryDsl {
    private final JPAQueryFactory queryFactory;

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
}
