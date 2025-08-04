package com.example.Veco.domain.external.repository;

import com.example.Veco.domain.external.converter.ExternalConverter;
import com.example.Veco.domain.external.dto.paging.ExternalCursor;
import com.example.Veco.domain.external.dto.response.ExternalResponseDTO;
import com.example.Veco.domain.external.dto.response.ExternalGroupedResponseDTO;
import com.example.Veco.domain.external.dto.paging.ExternalSearchCriteria;
import com.example.Veco.domain.external.entity.External;
import com.example.Veco.domain.external.entity.QExternal;
import com.example.Veco.global.apiPayload.page.CursorPage;
import com.example.Veco.global.enums.State;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class ExternalCursorRepository implements ExternalCustomRepository{

    private final JPAQueryFactory queryFactory;
    private final QExternal external = QExternal.external;

    public ExternalCursorRepository(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    // TODO : 메서드 테스트 필요
    @Override
    public CursorPage<ExternalResponseDTO.ExternalDTO> findExternalWithCursor(ExternalSearchCriteria criteria, String cursor, int size) {
        JPAQuery<External> query = queryFactory.selectFrom(external)
                .where(
                        buildFilterConditions(criteria),
                        buildCursorCondition(criteria, cursor)
                )
                .orderBy(buildOrderClause(criteria))
                .limit(size + 1);

        List<External> externals = query.fetch();
        return buildCursorPage(externals, size, criteria);
    }

    private BooleanExpression buildFilterConditions(ExternalSearchCriteria criteria) {
        BooleanExpression teamCondition = external.team.id.eq(criteria.getTeamId());
        
        BooleanExpression filterCondition = switch (criteria.getActiveFilterType()){
            case STATE -> external.state.eq(criteria.getState());
            case PRIORITY -> external.priority.eq(criteria.getPriority());
            case ASSIGNEE -> external.assignments.any().assignee.id.eq(criteria.getAssigneeId());
            case EXT_TYPE -> external.type.eq(criteria.getExtServiceType());
            case GOAL -> external.goal.id.eq(criteria.getGoalId());
            case NONE -> null;
        };
        
        return filterCondition != null ? teamCondition.and(filterCondition) : teamCondition;
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
            NumberExpression<Integer> statusPriorityExpr = getStatusPriorityExpression();

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

    private CursorPage<ExternalResponseDTO.ExternalDTO> buildCursorPage(List<External> externals, int size, ExternalSearchCriteria criteria) {

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


        List<ExternalResponseDTO.ExternalDTO> externalDTOS = externals.stream()
                .map(e -> ExternalConverter.toExternalDTO(e, e.getAssignments())).toList();

        return CursorPage.of(externalDTOS, nextCursor, hasNext);
    }

    private ExternalCursor createCursorFromExternal(External last, ExternalSearchCriteria criteria) {
        ExternalCursor externalCursor = new ExternalCursor();
        externalCursor.setId(last.getId());
        externalCursor.setCreatedAt(last.getCreatedAt());
        externalCursor.setIsStatusFiltered(criteria.getState() != null);

        if (criteria.getState() == null) {
            Integer statePriority = switch (last.getState()) {
                case NONE -> 1;
                case TODO -> 2;
                case IN_PROGRESS -> 3;
                case FINISH -> 4;
                case REVIEW -> 5;
                default -> null;
            };
            externalCursor.setStatusPriority(statePriority);
        }
        return externalCursor;
    }

    private OrderSpecifier<?>[] buildOrderClause(ExternalSearchCriteria criteria) {
        if (criteria.getState() != null) {
            // 상태 필터링이 있는 경우: 생성일 내림차순, ID 오름차순
            return new OrderSpecifier[]{
                    external.createdAt.desc(),
                    external.id.asc()
            };
        } else {
            // 상태 필터링이 없는 경우: 상태 우선순위, 생성일 내림차순, ID 오름차순
            NumberExpression<Integer> statusPriorityExpr = getStatusPriorityExpression();
            
            return new OrderSpecifier[]{
                    statusPriorityExpr.asc(),
                    external.createdAt.desc(),
                    external.id.asc()
            };
        }
    }

    private NumberExpression<Integer> getStatusPriorityExpression() {
        return new CaseBuilder()
                .when(external.state.eq(State.NONE)).then(1)
                .when(external.state.eq(State.TODO)).then(2)
                .when(external.state.eq(State.IN_PROGRESS)).then(3)
                .when(external.state.eq(State.FINISH)).then(4)
                .when(external.state.eq(State.REVIEW)).then(5)
                .otherwise(6);
    }

    public ExternalGroupedResponseDTO.ExternalGroupedPageResponse findExternalWithGroupedResponse(ExternalSearchCriteria criteria, String cursor, int size) {
        JPAQuery<External> query = queryFactory.selectFrom(external)
                .leftJoin(external.assignments).fetchJoin()
                .where(
                        buildFilterConditions(criteria),
                        buildCursorCondition(criteria, cursor)
                )
                .orderBy(buildOrderClause(criteria))
                .limit(size + 1);

        List<External> externals = query.fetch();
        
        boolean hasNext = externals.size() > size;
        if(hasNext) {
            externals = externals.subList(0, size);
        }

        String nextCursor = null;
        if(hasNext && !externals.isEmpty()) {
            External last = externals.getLast();
            ExternalCursor externalCursor = createCursorFromExternal(last, criteria);
            nextCursor = externalCursor.encode();
        }

        return ExternalConverter.toGroupedPageResponse(externals, hasNext, nextCursor, size);
    }

}
