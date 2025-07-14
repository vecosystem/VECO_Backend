package com.example.Veco.domain.external.repository;

import com.example.Veco.domain.external.dto.ExternalCursor;
import com.example.Veco.domain.external.dto.ExternalSearchCriteria;
import com.example.Veco.domain.external.entity.External;
import com.example.Veco.domain.external.entity.QExternal;
import com.example.Veco.domain.mapping.QAssignment;
import com.example.Veco.global.apiPayload.page.CursorPage;
import com.example.Veco.global.enums.State;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class ExternalCursorRepository implements ExternalCustomRepository{

    private final JPAQueryFactory queryFactory;
    private final QExternal external = QExternal.external;
    private final QAssignment assignment = QAssignment.assignment;

    public ExternalCursorRepository(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public CursorPage<External> findExternalWithCursor(ExternalSearchCriteria criteria, String cursor, int size) {
        JPAQuery<External> query = queryFactory.selectFrom(external)
                .where(
                        buildWhereClause(criteria),
                        buildCursorCondition(criteria, cursor)
                )
                .orderBy(buildOrderClause(criteria))
                .limit(size + 1);

        List<External> externals = query.fetch();
        return buildCursorPage(externals, size, criteria);
    }

    private BooleanExpression buildWhereClause(ExternalSearchCriteria criteria) {

        return switch (criteria.getActiveFilterType()){
            case STATE -> external.state.eq(criteria.getState());
            case PRIORITY -> external.priority.eq(criteria.getPriority());
            case ASSIGNEE -> external.assignments.any().assignee.id.eq(criteria.getAssigneeId());
            case NONE -> null;
        };

    }

    private BooleanExpression buildCursorCondition(ExternalSearchCriteria criteria, String cursor) {

        if(cursor == null) return null;

        ExternalCursor decodedCursor = ExternalCursor.decode(cursor);
        boolean hasStateFilter = criteria.getState() != null;

        if(hasStateFilter) {
            return external.createdAt.eq(decodedCursor.getCreatedAt())
                    .or(
                            external.createdAt.eq(decodedCursor.getCreatedAt())
                                    .and(external.id.gt(decodedCursor.getId()))
                    );
        }else{
            NumberExpression<Integer> statusPriorityExpr = new CaseBuilder()
                    .when(external.state.eq(State.IN_PROGRESS)).then(1)
                    .when(external.state.eq(State.TODO)).then(2)
                    .when(external.state.eq(State.DONE)).then(3)
                    .otherwise(4);

            return statusPriorityExpr.gt(decodedCursor.getStatusPriority())
                    .or(
                            statusPriorityExpr.eq(decodedCursor.getStatusPriority())
                                    .and(external.createdAt.lt(decodedCursor.getCreatedAt()))
                    )
                    .or(
                            statusPriorityExpr.eq(decodedCursor.getStatusPriority())
                                    .and(external.createdAt.eq(decodedCursor.getCreatedAt()))
                                    .and(external.id.gt(decodedCursor.getId()))
                    );
        }

    }

    private CursorPage<External> buildCursorPage(List<External> externals, int size, ExternalSearchCriteria criteria) {

        boolean hasNext = externals.size() > size;

        if(hasNext) {
            externals = externals.subList(0, size);
        }

        String nextCursor = null;

        if(hasNext && !externals.isEmpty()) {
            External last = externals.getLast();
            ExternalCursor cursor = createCursorFromExternal(last, criteria);
            nextCursor = cursor.encode();
        }

        return CursorPage.of(externals, nextCursor, hasNext);
    }

    private ExternalCursor createCursorFromExternal(External last, ExternalSearchCriteria criteria) {
        ExternalCursor externalCursor = new ExternalCursor();
        externalCursor.setId(last.getId());
        externalCursor.setCreatedAt(last.getCreatedAt());
        externalCursor.setIsStatusFiltered(criteria.getState() != null);

        if (criteria.getState() == null) {
            Integer statePriority = switch (last.getState()) {
                case IN_PROGRESS -> 1;
                case TODO -> 2;
                case DONE -> 3;
                case REVIEW -> 4;
                default -> null;
            };
            externalCursor.setStatusPriority(statePriority);
        }
        return externalCursor;
    }

    private OrderSpecifier<?>[] buildOrderClause(ExternalSearchCriteria criteria) {
        return null;
    }

}
