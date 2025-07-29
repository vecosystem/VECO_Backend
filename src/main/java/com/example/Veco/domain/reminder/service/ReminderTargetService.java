package com.example.Veco.domain.reminder.service;

import com.example.Veco.domain.external.entity.QExternal;
import com.example.Veco.domain.goal.entity.QGoal;
import com.example.Veco.domain.issue.entity.QIssue;
import com.example.Veco.global.enums.State;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReminderTargetService {

    private final JPAQueryFactory queryFactory;

    public List<Long> findDueIssueIds(LocalDate today) {
        QIssue issue = QIssue.issue;
        return queryFactory
                .select(issue.id)
                .from(issue)
                .where(
                        issue.deadlineEnd.eq(today),
                        issue.state.in(State.NONE, State.IN_PROGRESS, State.TODO)
                )
                .fetch();
    }

    public List<Long> findDueGoalIds(LocalDate today) {
        QGoal goal = QGoal.goal;
        return queryFactory
                .select(goal.id)
                .from(goal)
                .where(
                        goal.deadlineEnd.eq(today),
                        goal.state.in(State.NONE, State.IN_PROGRESS, State.TODO)
                )
                .fetch();
    }

    public List<Long> findDueExternalIds(LocalDate today) {
        QExternal external = QExternal.external;
        return queryFactory
                .select(external.id)
                .from(external)
                .where(
                        external.endDate.eq(today),
                        external.state.in(State.NONE, State.IN_PROGRESS, State.TODO)
                )
                .fetch();
    }
}
