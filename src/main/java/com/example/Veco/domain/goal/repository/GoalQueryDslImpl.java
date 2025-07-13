package com.example.Veco.domain.goal.repository;

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
