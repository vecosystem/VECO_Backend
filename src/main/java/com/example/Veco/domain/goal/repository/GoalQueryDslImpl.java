package com.example.Veco.domain.goal.repository;

import com.example.Veco.domain.assignee.entity.QAssignee;
import com.example.Veco.domain.goal.dto.response.GoalResDTO;
import com.example.Veco.domain.goal.entity.QGoal;
import com.example.Veco.domain.goal.exception.GoalException;
import com.example.Veco.domain.goal.exception.code.GoalErrorCode;
import com.example.Veco.domain.member.entity.QMember;
import com.example.Veco.global.enums.Category;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class GoalQueryDslImpl implements GoalQueryDsl {

    // QueryDSL
    private final JPAQueryFactory queryFactory;

    // 팀 내 모든 목표 조회
    @Override
    public List<GoalResDTO.SimpleGoal> findGoalsByTeamId(
            Predicate query,
            int size
    ) {
        // 객체 설정
        QGoal goal = QGoal.goal;
        QAssignee assignee = QAssignee.assignee;
        QMember member = QMember.member;

        // 조회
        List<GoalResDTO.SimpleGoal> result = queryFactory
                .from(goal)
                .where(query)
                .leftJoin(assignee).on(assignee.type.eq(Category.GOAL).and(assignee.targetId.eq(goal.id)))
                .leftJoin(member).on(member.eq(assignee.memberTeam.member))
                .orderBy(goal.id.desc())
                .groupBy(goal.id, assignee.id)
                .transform(GroupBy.groupBy(goal.id).list(
                        Projections.constructor(
                                GoalResDTO.SimpleGoal.class,
                                goal.id,
                                goal.name,
                                goal.title,
                                goal.state,
                                goal.priority,
                                Projections.constructor(
                                        GoalResDTO.Deadline.class,
                                        goal.deadlineStart,
                                        goal.deadlineEnd
                                ),
                                Projections.bean(
                                        GoalResDTO.QData.class,
                                        assignee.count().as("cnt"),
                                        GroupBy.list(
                                                Projections.constructor(
                                                        GoalResDTO.ManagerInfo.class,
                                                        member.profile.profileUrl,
                                                        member.name
                                                )
                                        ).as("info")
                                )
                        )
                ));

        // 목표가 없는 경우 throw
        if (result.isEmpty()){
            throw new GoalException(GoalErrorCode.NOT_FOUND_IN_TEAM);
        }

        // 담당자 수 정확히 설정
        for (GoalResDTO.SimpleGoal value : result) {
            value.managers().setCnt((long) value.managers().getInfo().size());
        }
        return result;
    }

    // 필터에 맞는 모든 목표 개수 조회
    @Override
    public Long findGoalsCountByFilter(
            Predicate query
    ){
        // 객체 생성
        QGoal goal = QGoal.goal;

        return queryFactory
                .select(goal.countDistinct())
                .from(goal)
                .where(query)
                .fetchFirst();
    }

    // 모든 목표 담당자 리스트 조회
    @Override
    public List<String> findGoalsAssigneeInTeam(
            Long teamId
    ){
        // 객체 생성
        QGoal goal = QGoal.goal;
        QAssignee assignee = QAssignee.assignee;

        return queryFactory
                .select(assignee.memberTeam.member.name)
                .from(goal)
                .leftJoin(assignee).on(assignee.type.eq(Category.GOAL)
                        .and(assignee.targetId.eq(goal.id)))
                .where(goal.team.id.eq(teamId))
                .fetch();
    }
}
