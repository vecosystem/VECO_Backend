package com.example.Veco.domain.goal.repository;

import com.example.Veco.domain.assignee.entity.QAssignee;
import com.example.Veco.domain.goal.dto.response.GoalResDTO;
import com.example.Veco.domain.goal.entity.QGoal;
import com.example.Veco.domain.mapping.entity.QMemberTeam;
import com.example.Veco.domain.member.entity.QMember;
import com.example.Veco.domain.profile.entity.QProfile;
import com.example.Veco.global.enums.Category;
import com.querydsl.core.group.GroupBy;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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
        QMemberTeam memberTeam = QMemberTeam.memberTeam;
        QMember member = QMember.member;
        QProfile profile = QProfile.profile;

        // 조회
        List<GoalResDTO.SimpleGoal> result = queryFactory
                .from(goal)
                .where(query)
                .leftJoin(assignee).on(assignee.targetId.eq(goal.id).and(assignee.type.eq(Category.GOAL)))
                .leftJoin(memberTeam).on(memberTeam.id.eq(assignee.memberTeam.id))
                .leftJoin(member).on(member.id.eq(memberTeam.member.id))
                .leftJoin(profile).on(profile.id.eq(member.profile.id))
                .orderBy(goal.id.desc())
                .groupBy(
                        goal.id,
                        goal.name,
                        goal.title,
                        goal.state,
                        goal.priority,
                        goal.deadlineStart,
                        goal.deadlineEnd,
                        member.name,
                        profile.profileImageUrl
                )
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
                                                        profile.profileImageUrl,
                                                        member.name
                                                )
                                        ).as("info")
                                )
                        )
                ));

        // 담당자 수 정확히 설정
        for (GoalResDTO.SimpleGoal value : result) {
            // 담당자 이름이 없는 경우 = 담당자가 없는 경우
            if (value.managers().getInfo().getFirst().name() == null){
                value.managers().setInfo(new ArrayList<>());
            }
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
