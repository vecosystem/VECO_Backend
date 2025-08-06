package com.example.Veco.domain.external.repository;

import com.example.Veco.domain.external.converter.ExternalConverter;
import com.example.Veco.domain.external.dto.paging.ExternalCursor;
import com.example.Veco.domain.external.dto.response.ExternalResponseDTO;
import com.example.Veco.domain.external.dto.response.ExternalGroupedResponseDTO;
import com.example.Veco.domain.external.dto.paging.ExternalSearchCriteria;
import com.example.Veco.domain.external.dto.paging.ExternalSearchCriteria.FilterType;
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
            case STATE -> criteria.getState() != null ? external.state.eq(criteria.getState()) : null;
            case PRIORITY -> criteria.getPriority() != null ? external.priority.eq(criteria.getPriority()) : null;
            case ASSIGNEE -> criteria.getAssigneeId() != null ? external.assignments.any().assignee.id.eq(criteria.getAssigneeId()) : null;
            case EXT_TYPE -> criteria.getExtServiceType() != null ? external.type.eq(criteria.getExtServiceType()) : null;
            case GOAL -> criteria.getGoalId() != null ? external.goal.id.eq(criteria.getGoalId()) : null;
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
        FilterType filterType = criteria.getActiveFilterType();
        
        return switch (filterType) {
            case STATE -> findExternalsGroupedByState(criteria, cursor, size);
            case PRIORITY -> findExternalsGroupedByPriority(criteria, cursor, size);
            case ASSIGNEE -> findExternalsGroupedByAssignee(criteria, cursor, size);
            case EXT_TYPE -> findExternalsGroupedByExtType(criteria, cursor, size);
            case GOAL -> findExternalsGroupedByGoal(criteria, cursor, size);
            case NONE -> findExternalsGroupedByState(criteria, cursor, size); // 기본값은 상태별 그룹핑
        };
    }

    private ExternalGroupedResponseDTO.ExternalGroupedPageResponse findExternalsGroupedByState(ExternalSearchCriteria criteria, String cursor, int size) {
        State[] stateOrder = {State.NONE, State.IN_PROGRESS, State.TODO, State.FINISH, State.REVIEW};
        return findExternalsGroupedByField(criteria, cursor, size, stateOrder, "state");
    }

    private ExternalGroupedResponseDTO.ExternalGroupedPageResponse findExternalsGroupedByPriority(ExternalSearchCriteria criteria, String cursor, int size) {
        com.example.Veco.global.enums.Priority[] priorityOrder = {
            com.example.Veco.global.enums.Priority.NONE, 
            com.example.Veco.global.enums.Priority.URGENT, 
            com.example.Veco.global.enums.Priority.HIGH, 
            com.example.Veco.global.enums.Priority.NORMAL, 
            com.example.Veco.global.enums.Priority.LOW
        };
        return findExternalsGroupedByField(criteria, cursor, size, priorityOrder, "priority");
    }

    private ExternalGroupedResponseDTO.ExternalGroupedPageResponse findExternalsGroupedByAssignee(ExternalSearchCriteria criteria, String cursor, int size) {
        JPAQuery<External> baseQuery = queryFactory.selectFrom(external)
                .leftJoin(external.assignments).fetchJoin()
                .where(external.team.id.eq(criteria.getTeamId()));

        return buildGroupedResponseForAssignee(baseQuery, cursor, size, criteria);
    }

    private ExternalGroupedResponseDTO.ExternalGroupedPageResponse findExternalsGroupedByExtType(ExternalSearchCriteria criteria, String cursor, int size) {
        com.example.Veco.global.enums.ExtServiceType[] extTypeOrder = com.example.Veco.global.enums.ExtServiceType.values();
        return findExternalsGroupedByField(criteria, cursor, size, extTypeOrder, "extType");
    }

    private ExternalGroupedResponseDTO.ExternalGroupedPageResponse findExternalsGroupedByGoal(ExternalSearchCriteria criteria, String cursor, int size) {
        JPAQuery<External> baseQuery = queryFactory.selectFrom(external)
                .leftJoin(external.goal).fetchJoin()
                .where(external.team.id.eq(criteria.getTeamId()));

        return buildGroupedResponseForGoal(baseQuery, cursor, size, criteria);
    }

    private <T extends Enum<T>> ExternalGroupedResponseDTO.ExternalGroupedPageResponse findExternalsGroupedByField(
            ExternalSearchCriteria criteria, String cursor, int size, T[] enumOrder, String fieldType) {
        
        List<ExternalGroupedResponseDTO.FilteredExternalGroup> groups = new java.util.ArrayList<>();
        int totalFetched = 0;
        boolean hasNext = false;
        String nextCursor = null;
        
        ExternalCursor decodedCursor = cursor != null ? ExternalCursor.decode(cursor) : null;
        
        for (T enumValue : enumOrder) {
            if (totalFetched >= size) {
                hasNext = true;
                break;
            }
            
            BooleanExpression condition = external.team.id.eq(criteria.getTeamId());
            condition = condition.and(buildEnumCondition(enumValue, fieldType));
            
            JPAQuery<External> query = queryFactory.selectFrom(external)
                    .leftJoin(external.assignments).fetchJoin()
                    .where(condition);
            
            if (decodedCursor != null && shouldSkipGroup(enumValue, decodedCursor, fieldType)) {
                // 건너뛰는 그룹도 빈 그룹으로 추가
                ExternalGroupedResponseDTO.FilteredExternalGroup emptyGroup = ExternalGroupedResponseDTO.FilteredExternalGroup.builder()
                        .filterName(getDisplayName(enumValue))
                        .dataCnt(0)
                        .externals(new java.util.ArrayList<>())
                        .build();
                groups.add(emptyGroup);
                continue;
            }
            
            if (decodedCursor != null && isSameGroup(enumValue, decodedCursor, fieldType)) {
                query = query.where(buildCursorConditionForGroup(decodedCursor));
            }
            
            query = query.orderBy(external.createdAt.desc(), external.id.asc());
            
            int remainingSize = size - totalFetched;
            List<External> groupExternals = query.limit(remainingSize + 1).fetch();
            
            // 데이터가 있든 없든 항상 그룹을 추가
            boolean groupHasNext = groupExternals.size() > remainingSize;
            if (groupHasNext) {
                groupExternals = groupExternals.subList(0, remainingSize);
                hasNext = true;
                if (!groupExternals.isEmpty()) {
                    External lastExternal = groupExternals.getLast();
                    ExternalCursor groupCursor = new ExternalCursor();
                    groupCursor.setId(lastExternal.getId());
                    groupCursor.setCreatedAt(lastExternal.getCreatedAt());
                    groupCursor.setGroupValue(enumValue.name());
                    nextCursor = groupCursor.encode();
                }
            }
            
//            List<ExternalResponseDTO.ExternalDTO> externalDTOs = groupExternals.stream()
//                    .map(e -> ExternalConverter.toExternalDTO(e, e.getAssignments()))
//                    .toList();

            List<ExternalGroupedResponseDTO.ExternalItemDTO> externalDTOs = groupExternals.stream().map(
                    ExternalConverter::toExternalItemDTO
            ).toList();

            ExternalGroupedResponseDTO.FilteredExternalGroup group = ExternalGroupedResponseDTO.FilteredExternalGroup.builder()
                    .filterName(getDisplayName(enumValue))
                    .dataCnt(externalDTOs.size())
                    .externals(externalDTOs)
                    .build();
                    
            groups.add(group);
            totalFetched += groupExternals.size();
        }
        
        return ExternalGroupedResponseDTO.ExternalGroupedPageResponse.builder()
                .data(groups)
                .hasNext(hasNext)
                .nextCursor(nextCursor)
                .pageSize(size)
                .build();
    }

    private BooleanExpression buildEnumCondition(Enum<?> enumValue, String fieldType) {
        return switch (fieldType) {
            case "state" -> external.state.eq((State) enumValue);
            case "priority" -> external.priority.eq((com.example.Veco.global.enums.Priority) enumValue);
            case "extType" -> external.type.eq((com.example.Veco.global.enums.ExtServiceType) enumValue);
            default -> null;
        };
    }

    private boolean shouldSkipGroup(Enum<?> enumValue, ExternalCursor cursor, String fieldType) {
        if (cursor.getGroupValue() == null) return false;
        
        String[] fieldOrder = getFieldOrder(fieldType);
        int currentIndex = java.util.Arrays.asList(fieldOrder).indexOf(enumValue.name());
        int cursorIndex = java.util.Arrays.asList(fieldOrder).indexOf(cursor.getGroupValue());
        
        return currentIndex < cursorIndex;
    }

    private boolean isSameGroup(Enum<?> enumValue, ExternalCursor cursor, String fieldType) {
        return cursor.getGroupValue() != null && cursor.getGroupValue().equals(enumValue.name());
    }

    private String[] getFieldOrder(String fieldType) {
        return switch (fieldType) {
            case "state" -> new String[]{"NONE", "IN_PROGRESS", "TODO", "FINISH", "REVIEW"};
            case "priority" -> new String[]{"NONE", "URGENT", "HIGH", "NORMAL", "LOW"};
            case "extType" -> java.util.Arrays.stream(com.example.Veco.global.enums.ExtServiceType.values()).map(Enum::name).toArray(String[]::new);
            default -> new String[]{};
        };
    }

    private BooleanExpression buildCursorConditionForGroup(ExternalCursor cursor) {
        return external.createdAt.lt(cursor.getCreatedAt())
                .or(external.createdAt.eq(cursor.getCreatedAt()).and(external.id.gt(cursor.getId())));
    }

    private String getDisplayName(Enum<?> enumValue) {
        return enumValue.name();
    }

    private List<ExternalGroupedResponseDTO.ExternalItemDTO> convertToExternalItemDTOs(List<ExternalResponseDTO.ExternalDTO> externalDTOs) {
        return externalDTOs.stream()
                .map(dto -> ExternalGroupedResponseDTO.ExternalItemDTO.builder()
                        .id(dto.getId())
                        .name(dto.getName())
                        .title(dto.getTitle())
                        .state(dto.getState())
                        .priority(dto.getPriority() != null ? dto.getPriority().name() : "없음")
                        .deadline(dto.getDeadlines() != null ? 
                            ExternalGroupedResponseDTO.DeadlineDTO.builder()
                                .start(dto.getDeadlines().getStart() != null ? dto.getDeadlines().getStart().toString() : null)
                                .end(dto.getDeadlines().getEnd() != null ? dto.getDeadlines().getEnd().toString() : null)
                                .build() : null)
                        .managers(dto.getManagers() != null ?
                            ExternalGroupedResponseDTO.ManagersDTO.builder()
                                .cnt(dto.getManagers().getCnt())
                                .info(dto.getManagers().getInfo().stream()
                                    .map(info -> ExternalGroupedResponseDTO.ManagerInfoDTO.builder()
                                        .profileUrl(info.getProfileUrl())
                                        .managerName(info.getNickname())
                                        .build())
                                    .toList())
                                .build() : null)
                        .extServiceType(dto.getExtServiceType())
                        .build())
                .toList();
    }

    private ExternalGroupedResponseDTO.ExternalGroupedPageResponse buildGroupedResponseForAssignee(JPAQuery<External> baseQuery, String cursor, int size, ExternalSearchCriteria criteria) {
        return ExternalGroupedResponseDTO.ExternalGroupedPageResponse.builder()
                .data(new java.util.ArrayList<>())
                .hasNext(false)
                .nextCursor(null)
                .pageSize(size)
                .build();
    }

    private ExternalGroupedResponseDTO.ExternalGroupedPageResponse buildGroupedResponseForGoal(JPAQuery<External> baseQuery, String cursor, int size, ExternalSearchCriteria criteria) {
        return ExternalGroupedResponseDTO.ExternalGroupedPageResponse.builder()
                .data(new java.util.ArrayList<>())
                .hasNext(false)
                .nextCursor(null)
                .pageSize(size)
                .build();
    }

}
