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
        // Assignment 테이블을 위한 별도의 Q 인스턴스 생성
        com.example.Veco.domain.mapping.QAssignment assignment = com.example.Veco.domain.mapping.QAssignment.assignment;
        
        // 먼저 팀에 속한 모든 담당자들을 조회 (Assignment 테이블에서)
        List<com.querydsl.core.Tuple> assigneeResults = queryFactory
                .select(assignment.assignee.id, assignment.assignee.name)
                .from(external)
                .leftJoin(external.assignments, assignment)
                .leftJoin(assignment.assignee)
                .where(external.team.id.eq(criteria.getTeamId())
                        .and(external.deletedAt.isNull())
                        .and(assignment.assignee.id.isNotNull()))
                .groupBy(assignment.assignee.id, assignment.assignee.name)
                .orderBy(assignment.assignee.name.asc())
                .fetch();

        List<ExternalGroupedResponseDTO.FilteredExternalGroup> groups = new java.util.ArrayList<>();
        int totalFetched = 0;
        boolean hasNext = false;
        String nextCursor = null;
        
        ExternalCursor decodedCursor = cursor != null ? ExternalCursor.decode(cursor) : null;
        
        // "담당자 없음" 그룹 처리 (담당자가 없는 외부 이슈들)
        if (totalFetched < size) {
            String displayName = "담당자 없음";
            
            // 커서 기반 필터링
            if (decodedCursor == null || !shouldSkipAssigneeGroup("NULL", decodedCursor)) {
                // 담당자가 없는 외부 이슈들 조회
                JPAQuery<External> noAssigneeQuery = queryFactory.selectFrom(external)
                        .leftJoin(external.assignments).fetchJoin()
                        .where(external.team.id.eq(criteria.getTeamId())
                                .and(external.deletedAt.isNull())
                                .and(external.assignments.isEmpty()));
                
                if (decodedCursor != null && isSameAssigneeGroup("NULL", decodedCursor)) {
                    noAssigneeQuery = noAssigneeQuery.where(buildCursorConditionForGroup(decodedCursor));
                }
                
                noAssigneeQuery = noAssigneeQuery.orderBy(external.createdAt.desc(), external.id.asc());
                
                int remainingSize = size - totalFetched;
                List<External> groupExternals = noAssigneeQuery.limit(remainingSize + 1).fetch();
                
                boolean groupHasNext = groupExternals.size() > remainingSize;
                if (groupHasNext) {
                    groupExternals = groupExternals.subList(0, remainingSize);
                    hasNext = true;
                    if (!groupExternals.isEmpty()) {
                        External lastExternal = groupExternals.getLast();
                        ExternalCursor groupCursor = new ExternalCursor();
                        groupCursor.setId(lastExternal.getId());
                        groupCursor.setCreatedAt(lastExternal.getCreatedAt());
                        groupCursor.setGroupValue("NULL");
                        nextCursor = groupCursor.encode();
                    }
                }
                
                List<ExternalGroupedResponseDTO.ExternalItemDTO> externalDTOs = groupExternals.stream().map(
                        ExternalConverter::toExternalItemDTO
                ).toList();

                ExternalGroupedResponseDTO.FilteredExternalGroup group = ExternalGroupedResponseDTO.FilteredExternalGroup.builder()
                        .filterName(displayName)
                        .dataCnt(externalDTOs.size())
                        .externals(externalDTOs)
                        .build();
                        
                groups.add(group);
                totalFetched += groupExternals.size();
            } else {
                // 건너뛰는 그룹도 빈 그룹으로 추가
                ExternalGroupedResponseDTO.FilteredExternalGroup emptyGroup = ExternalGroupedResponseDTO.FilteredExternalGroup.builder()
                        .filterName(displayName)
                        .dataCnt(0)
                        .externals(new java.util.ArrayList<>())
                        .build();
                groups.add(emptyGroup);
            }
        }
        
        // 담당자별 그룹 처리
        for (com.querydsl.core.Tuple assigneeResult : assigneeResults) {
            if (totalFetched >= size) {
                hasNext = true;
                break;
            }
            
            Long assigneeId = assigneeResult.get(assignment.assignee.id);
            String assigneeName = assigneeResult.get(assignment.assignee.name);
            String displayName = assigneeName != null ? assigneeName : "Unknown";
            
            // 커서 기반 필터링  
            if (decodedCursor != null && assigneeId != null && shouldSkipAssigneeGroup(assigneeId.toString(), decodedCursor)) {
                // 건너뛰는 그룹도 빈 그룹으로 추가
                ExternalGroupedResponseDTO.FilteredExternalGroup emptyGroup = ExternalGroupedResponseDTO.FilteredExternalGroup.builder()
                        .filterName(displayName)
                        .dataCnt(0)
                        .externals(new java.util.ArrayList<>())
                        .build();
                groups.add(emptyGroup);
                continue;
            }
            
            // 해당 담당자가 담당하는 외부 이슈들 조회
            com.example.Veco.domain.mapping.QAssignment assignmentForFilter = new com.example.Veco.domain.mapping.QAssignment("assignmentForFilter");
            JPAQuery<External> query = queryFactory.selectFrom(external)
                    .leftJoin(external.assignments).fetchJoin()
                    .leftJoin(external.assignments, assignmentForFilter)
                    .where(external.team.id.eq(criteria.getTeamId())
                            .and(external.deletedAt.isNull())
                            .and(assignmentForFilter.assignee.id.eq(assigneeId)));
            
            if (decodedCursor != null && assigneeId != null && isSameAssigneeGroup(assigneeId.toString(), decodedCursor)) {
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
                    groupCursor.setGroupValue(assigneeId != null ? assigneeId.toString() : "NULL");
                    nextCursor = groupCursor.encode();
                }
            }
            
            List<ExternalGroupedResponseDTO.ExternalItemDTO> externalDTOs = groupExternals.stream().map(
                    ExternalConverter::toExternalItemDTO
            ).toList();

            ExternalGroupedResponseDTO.FilteredExternalGroup group = ExternalGroupedResponseDTO.FilteredExternalGroup.builder()
                    .filterName(displayName)
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

    private ExternalGroupedResponseDTO.ExternalGroupedPageResponse findExternalsGroupedByExtType(ExternalSearchCriteria criteria, String cursor, int size) {
        com.example.Veco.global.enums.ExtServiceType[] extTypeOrder = com.example.Veco.global.enums.ExtServiceType.values();
        return findExternalsGroupedByField(criteria, cursor, size, extTypeOrder, "extType");
    }

    private ExternalGroupedResponseDTO.ExternalGroupedPageResponse findExternalsGroupedByGoal(ExternalSearchCriteria criteria, String cursor, int size) {
        // 먼저 팀에 속한 모든 목표들을 조회
        List<com.querydsl.core.Tuple> goalResults = queryFactory
                .select(external.goal.id, external.goal.name)
                .from(external)
                .leftJoin(external.goal)
                .where(external.team.id.eq(criteria.getTeamId()).and(external.deletedAt.isNull()))
                .groupBy(external.goal.id, external.goal.name)
                .orderBy(external.goal.name.asc().nullsFirst()) // null 목표(목표 없음)를 먼저 표시
                .fetch();

        List<ExternalGroupedResponseDTO.FilteredExternalGroup> groups = new java.util.ArrayList<>();
        int totalFetched = 0;
        boolean hasNext = false;
        String nextCursor = null;
        
        ExternalCursor decodedCursor = cursor != null ? ExternalCursor.decode(cursor) : null;
        
        for (com.querydsl.core.Tuple goalResult : goalResults) {
            if (totalFetched >= size) {
                hasNext = true;
                break;
            }
            
            Long goalId = goalResult.get(external.goal.id);
            String goalName = goalResult.get(external.goal.name);
            String displayName = goalName != null ? goalName : "목표 없음";
            
            // 커서 기반 필터링
            if (decodedCursor != null && shouldSkipGoalGroup(goalId, decodedCursor)) {
                // 건너뛰는 그룹도 빈 그룹으로 추가
                ExternalGroupedResponseDTO.FilteredExternalGroup emptyGroup = ExternalGroupedResponseDTO.FilteredExternalGroup.builder()
                        .filterName(displayName)
                        .dataCnt(0)
                        .externals(new java.util.ArrayList<>())
                        .build();
                groups.add(emptyGroup);
                continue;
            }
            
            // 해당 목표의 외부 이슈들 조회
            BooleanExpression goalCondition = goalId != null ? 
                external.goal.id.eq(goalId) : external.goal.isNull();
            
            JPAQuery<External> query = queryFactory.selectFrom(external)
                    .leftJoin(external.assignments).fetchJoin()
                    .leftJoin(external.goal).fetchJoin()
                    .where(external.team.id.eq(criteria.getTeamId())
                            .and(external.deletedAt.isNull())
                            .and(goalCondition));
            
            if (decodedCursor != null && isSameGoalGroup(goalId, decodedCursor)) {
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
                    groupCursor.setGroupValue(goalId != null ? goalId.toString() : "NULL");
                    nextCursor = groupCursor.encode();
                }
            }
            
            List<ExternalGroupedResponseDTO.ExternalItemDTO> externalDTOs = groupExternals.stream().map(
                    ExternalConverter::toExternalItemDTO
            ).toList();

            ExternalGroupedResponseDTO.FilteredExternalGroup group = ExternalGroupedResponseDTO.FilteredExternalGroup.builder()
                    .filterName(displayName)
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
            
            BooleanExpression condition = external.team.id.eq(criteria.getTeamId()).and(external.deletedAt.isNull());
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

    private boolean shouldSkipGoalGroup(Long goalId, ExternalCursor cursor) {
        if (cursor.getGroupValue() == null) return false;
        
        String currentGoalValue = goalId != null ? goalId.toString() : "NULL";
        
        // 커서의 목표보다 이전 목표인지 확인 (목표 없음이 먼저, 그 다음은 ID 순)
        if ("NULL".equals(cursor.getGroupValue())) {
            return false; // 목표 없음이 첫 번째이므로 건너뛸 목표 없음
        }
        
        if ("NULL".equals(currentGoalValue)) {
            return true; // 현재가 목표 없음인데 커서는 특정 목표를 가리키므로 건너뛰기
        }
        
        try {
            Long cursorGoalId = Long.parseLong(cursor.getGroupValue());
            return goalId < cursorGoalId;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private boolean isSameGoalGroup(Long goalId, ExternalCursor cursor) {
        if (cursor.getGroupValue() == null) return false;
        
        String currentGoalValue = goalId != null ? goalId.toString() : "NULL";
        return cursor.getGroupValue().equals(currentGoalValue);
    }
    
    private boolean shouldSkipAssigneeGroup(String assigneeValue, ExternalCursor cursor) {
        if (cursor.getGroupValue() == null) return false;
        
        // 담당자 없음("NULL")이 먼저, 그 다음은 담당자 ID 순
        if ("NULL".equals(cursor.getGroupValue())) {
            return false; // 담당자 없음이 첫 번째이므로 건너뛸 그룹 없음
        }
        
        if ("NULL".equals(assigneeValue)) {
            return true; // 현재가 담당자 없음인데 커서는 특정 담당자를 가리키므로 건너뛰기
        }
        
        try {
            Long currentAssigneeId = Long.parseLong(assigneeValue);
            Long cursorAssigneeId = Long.parseLong(cursor.getGroupValue());
            return currentAssigneeId < cursorAssigneeId;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    
    private boolean isSameAssigneeGroup(String assigneeValue, ExternalCursor cursor) {
        if (cursor.getGroupValue() == null) return false;
        return cursor.getGroupValue().equals(assigneeValue);
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
