package com.example.Veco.domain.goal.repository;

import com.example.Veco.domain.assignee.entity.QAssignee;
import com.example.Veco.domain.comment.entity.QComment;
import com.example.Veco.domain.comment.entity.QCommentRoom;
import com.example.Veco.domain.goal.dto.response.GoalResDTO;
import com.example.Veco.domain.goal.entity.QGoal;
import com.example.Veco.domain.issue.entity.QIssue;
import com.example.Veco.domain.member.entity.QProfile;
import com.example.Veco.global.enums.Category;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GoalQueryDslImpl implements GoalQueryDsl {

    // QueryDSL
    private final JPAQueryFactory queryFactory;

    // 팀 내 모든 목표 조회
}
