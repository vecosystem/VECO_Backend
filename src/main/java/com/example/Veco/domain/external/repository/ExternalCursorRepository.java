package com.example.Veco.domain.external.repository;

import com.example.Veco.domain.external.converter.ExternalConverter;
import com.example.Veco.domain.external.dto.paging.ExternalCursor;
import com.example.Veco.domain.external.dto.response.ExternalResponseDTO;
import com.example.Veco.domain.external.dto.response.ExternalGroupedResponseDTO;
import com.example.Veco.domain.external.dto.paging.ExternalSearchCriteria;
import com.example.Veco.domain.external.dto.paging.ExternalSearchCriteria.FilterType;
import com.example.Veco.domain.external.entity.External;
import com.example.Veco.domain.external.entity.QExternal;
import com.example.Veco.global.enums.State;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Slf4j
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
        com.example.Veco.domain.mapping.QAssignment assignment = com.example.Veco.domain.mapping.QAssignment.assignment;

        // 먼저 팀에 속한 모든 담당자들을 조회 (정렬된 순서)
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

        // "담당자 없음" + 일반 담당자들의 전체 리스트 생성
        List<AssigneeInfo> allAssignees = new java.util.ArrayList<>();
        // 담당자 없음을 첫 번째로 추가
        allAssignees.add(new AssigneeInfo(null, "담당자 없음"));
        // 일반 담당자들 추가
        for (com.querydsl.core.Tuple result : assigneeResults) {
            Long assigneeId = result.get(assignment.assignee.id);
            String assigneeName = result.get(assignment.assignee.name);
            allAssignees.add(new AssigneeInfo(assigneeId, assigneeName != null ? assigneeName : "Unknown"));
        }

        // 커서가 있는 경우 시작 인덱스 찾기
        int startIndex = 0;
        if (decodedCursor != null && decodedCursor.getGroupValue() != null) {
            startIndex = findAssigneeStartIndex(allAssignees, decodedCursor.getGroupValue());
            log.info("Found start index: {} for cursor group: {}", startIndex, decodedCursor.getGroupValue());
        }

        // 시작 인덱스부터 처리
        for (int i = startIndex; i < allAssignees.size(); i++) {
            AssigneeInfo assigneeInfo = allAssignees.get(i);

            if (totalFetched >= size) {
                hasNext = true;
                if (nextCursor == null) {
                    ExternalCursor groupCursor = new ExternalCursor();
                    groupCursor.setId(0L); // 다음 그룹의 시작점
                    groupCursor.setGroupValue(assigneeInfo.getId() != null ? assigneeInfo.getId().toString() : "NULL_GROUP");
                    nextCursor = groupCursor.encode();
                    log.info("Created next cursor for assignee: {}", assigneeInfo.getId());
                }
                break;
            }

            // 해당 담당자의 외부 이슈들 조회
            JPAQuery<External> query;

            if (assigneeInfo.getId() == null) {
                // 담당자가 없는 외부 이슈들 조회
                query = queryFactory.selectFrom(external)
                        .leftJoin(external.assignments).fetchJoin()
                        .where(external.team.id.eq(criteria.getTeamId())
                                .and(external.deletedAt.isNull())
                                .and(external.assignments.isEmpty()));
            } else {
                // 특정 담당자가 담당하는 외부 이슈들 조회
                com.example.Veco.domain.mapping.QAssignment assignmentForFilter = new com.example.Veco.domain.mapping.QAssignment("assignmentForFilter");
                query = queryFactory.selectFrom(external)
                        .leftJoin(external.assignments).fetchJoin()
                        .leftJoin(external.assignments, assignmentForFilter)
                        .where(external.team.id.eq(criteria.getTeamId())
                                .and(external.deletedAt.isNull())
                                .and(assignmentForFilter.assignee.id.eq(assigneeInfo.getId())));
            }

            // 같은 그룹인 경우 커서 조건 적용
            if (decodedCursor != null && isSameAssigneeGroup(assigneeInfo.getId(), decodedCursor)) {
                query = query.where(external.id.gt(decodedCursor.getId()));
                log.info("Applied cursor condition for assignee: {} with id > {}", assigneeInfo.getId(), decodedCursor.getId());
            }

            query = query.orderBy(external.id.asc());

            int remainingSize = size - totalFetched;
            List<External> groupExternals = query.limit(remainingSize + 1).fetch();

            // 그룹 내에서 페이지네이션 처리
            boolean groupHasNext = groupExternals.size() > remainingSize;
            if (groupHasNext) {
                groupExternals = groupExternals.subList(0, remainingSize);
                hasNext = true;
                if (!groupExternals.isEmpty()) {
                    External lastExternal = groupExternals.getLast();
                    ExternalCursor groupCursor = new ExternalCursor();
                    groupCursor.setId(lastExternal.getId());
                    groupCursor.setGroupValue(assigneeInfo.getId() != null ? assigneeInfo.getId().toString() : "NULL_GROUP");
                    nextCursor = groupCursor.encode();
                    log.info("Created cursor for same assignee: {} with lastId: {}", assigneeInfo.getId(), lastExternal.getId());
                }
            }

            List<ExternalGroupedResponseDTO.ExternalItemDTO> externalDTOs = groupExternals.stream()
                    .map(ExternalConverter::toExternalItemDTO)
                    .toList();

            ExternalGroupedResponseDTO.FilteredExternalGroup group = ExternalGroupedResponseDTO.FilteredExternalGroup.builder()
                    .filterName(assigneeInfo.getName())
                    .dataCnt(externalDTOs.size())
                    .externals(externalDTOs)
                    .build();

            groups.add(group);
            totalFetched += groupExternals.size();

            log.info("Assignee: {} ({}), fetched: {}, totalFetched: {}",
                    assigneeInfo.getName(), assigneeInfo.getId(), groupExternals.size(), totalFetched);
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
        // 먼저 팀에 속한 모든 목표들을 조회 (정렬된 순서 유지)
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

        // 커서가 있는 경우 시작 인덱스 찾기
        int startIndex = 0;
        if (decodedCursor != null && decodedCursor.getGroupValue() != null) {
            startIndex = findGoalStartIndex(goalResults, decodedCursor.getGroupValue());
            log.info("Found start index: {} for cursor group: {}", startIndex, decodedCursor.getGroupValue());
        }

        // 시작 인덱스부터 처리
        for (int i = startIndex; i < goalResults.size(); i++) {
            com.querydsl.core.Tuple goalResult = goalResults.get(i);

            if (totalFetched >= size) {
                hasNext = true;
                // 다음 그룹이 있으므로 현재 그룹의 첫 번째 항목으로 커서 생성
                Long goalId = goalResult.get(external.goal.id);
                if (nextCursor == null) {
                    ExternalCursor groupCursor = new ExternalCursor();
                    groupCursor.setId(0L); // 다음 그룹의 시작점
                    groupCursor.setGroupValue(goalId != null ? goalId.toString() : "NULL_GROUP");
                    nextCursor = groupCursor.encode();
                    log.info("Created next cursor for goal: {}", goalId);
                }
                break;
            }

            Long goalId = goalResult.get(external.goal.id);
            String goalName = goalResult.get(external.goal.name);
            String displayName = goalName != null ? goalName : "목표 없음";

            // 해당 목표의 외부 이슈들 조회
            BooleanExpression goalCondition = goalId != null ?
                    external.goal.id.eq(goalId) : external.goal.isNull();

            JPAQuery<External> query = queryFactory.selectFrom(external)
                    .leftJoin(external.assignments).fetchJoin()
                    .leftJoin(external.goal).fetchJoin()
                    .where(external.team.id.eq(criteria.getTeamId())
                            .and(external.deletedAt.isNull())
                            .and(goalCondition));

            // 같은 그룹인 경우 커서 조건 적용 (ID 기준으로 이후 데이터 조회)
            if (decodedCursor != null && isSameGoalGroup(goalId, decodedCursor)) {
                query = query.where(external.id.gt(decodedCursor.getId()));
                log.info("Applied cursor condition for goal: {} with id > {}", goalId, decodedCursor.getId());
            }

            query = query.orderBy(external.id.asc());

            int remainingSize = size - totalFetched;
            List<External> groupExternals = query.limit(remainingSize + 1).fetch();

            // 그룹 내에서 페이지네이션 처리
            boolean groupHasNext = groupExternals.size() > remainingSize;
            if (groupHasNext) {
                groupExternals = groupExternals.subList(0, remainingSize);
                hasNext = true;
                if (!groupExternals.isEmpty()) {
                    External lastExternal = groupExternals.getLast();
                    ExternalCursor groupCursor = new ExternalCursor();
                    groupCursor.setId(lastExternal.getId());
                    groupCursor.setGroupValue(goalId != null ? goalId.toString() : "NULL_GROUP");
                    nextCursor = groupCursor.encode();
                    log.info("Created cursor for same goal: {} with lastId: {}", goalId, lastExternal.getId());
                }
            }

            List<ExternalGroupedResponseDTO.ExternalItemDTO> externalDTOs = groupExternals.stream()
                    .map(ExternalConverter::toExternalItemDTO)
                    .toList();

            ExternalGroupedResponseDTO.FilteredExternalGroup group = ExternalGroupedResponseDTO.FilteredExternalGroup.builder()
                    .filterName(displayName)
                    .dataCnt(externalDTOs.size())
                    .externals(externalDTOs)
                    .build();

            groups.add(group);
            totalFetched += groupExternals.size();

            log.info("Goal: {} ({}), fetched: {}, totalFetched: {}",
                    displayName, goalId, groupExternals.size(), totalFetched);
        }

        return ExternalGroupedResponseDTO.ExternalGroupedPageResponse.builder()
                .data(groups)
                .hasNext(hasNext)
                .nextCursor(nextCursor)
                .pageSize(size)
                .build();
    }

    private int findGoalStartIndex(List<com.querydsl.core.Tuple> goalResults, String cursorGroupValue) {
        if ("NULL_GROUP".equals(cursorGroupValue)) {
            // NULL 목표를 찾기 (name이 null인 경우)
            for (int i = 0; i < goalResults.size(); i++) {
                String goalName = goalResults.get(i).get(QExternal.external.goal.name);
                if (goalName == null) {
                    return i;
                }
            }
        } else {
            // 특정 goalId를 찾기
            try {
                Long targetGoalId = Long.parseLong(cursorGroupValue);
                for (int i = 0; i < goalResults.size(); i++) {
                    Long goalId = goalResults.get(i).get(QExternal.external.goal.id);
                    if (targetGoalId.equals(goalId)) {
                        return i;
                    }
                }
            } catch (NumberFormatException e) {
                log.warn("Invalid goal ID in cursor: {}", cursorGroupValue);
            }
        }

        // 찾지 못한 경우 처음부터 시작
        log.warn("Goal not found for cursor: {}, starting from beginning", cursorGroupValue);
        return 0;
    }

    private <T extends Enum<T>> ExternalGroupedResponseDTO.ExternalGroupedPageResponse findExternalsGroupedByField(
            ExternalSearchCriteria criteria, String cursor, int size, T[] enumOrder, String fieldType) {

        List<ExternalGroupedResponseDTO.FilteredExternalGroup> groups = new java.util.ArrayList<>();
        int totalFetched = 0;
        boolean hasNext = false;
        String nextCursor = null;

        ExternalCursor decodedCursor = cursor != null ? ExternalCursor.decode(cursor) : null;

        // 커서가 있는 경우, 해당 그룹부터 시작하도록 시작 인덱스 계산
        int startIndex = 0;
        if (decodedCursor != null && decodedCursor.getGroupValue() != null) {
            String[] fieldOrder = getFieldOrder(fieldType);
            for (int i = 0; i < fieldOrder.length; i++) {
                if (fieldOrder[i].equals(decodedCursor.getGroupValue())) {
                    startIndex = i;
                    break;
                }
            }
        }

        for (int i = startIndex; i < enumOrder.length; i++) {
            T enumValue = enumOrder[i];

            if (totalFetched >= size) {
                hasNext = true;
                // 다음 그룹의 첫 번째 항목으로 커서 생성
                if (nextCursor == null && i < enumOrder.length) {
                    ExternalCursor groupCursor = new ExternalCursor();
                    groupCursor.setId(0L); // 다음 그룹의 시작점
                    groupCursor.setGroupValue(enumValue.name());
                    nextCursor = groupCursor.encode();
                }
                break;
            }

            BooleanExpression condition = external.team.id.eq(criteria.getTeamId())
                    .and(external.deletedAt.isNull())
                    .and(buildEnumCondition(enumValue, fieldType));

            JPAQuery<External> query = queryFactory.selectFrom(external)
                    .leftJoin(external.assignments).fetchJoin()
                    .where(condition);

            // 같은 그룹인 경우 커서 조건 적용
            if (decodedCursor != null && isSameGroup(enumValue, decodedCursor, fieldType)) {
                query = query.where(external.id.gt(decodedCursor.getId()));
                log.info("Applied cursor condition for group: {} with id > {}",
                        enumValue.name(), decodedCursor.getId());
            }

            query = query.orderBy(external.id.asc());

            int remainingSize = size - totalFetched;
            List<External> groupExternals = query.limit(remainingSize + 1).fetch();

            boolean groupHasNext = groupExternals.size() > remainingSize;
            if (groupHasNext) {
                groupExternals = groupExternals.subList(0, remainingSize);
                hasNext = true;
                if (!groupExternals.isEmpty()) {
                    External lastExternal = groupExternals.getLast();
                    ExternalCursor groupCursor = new ExternalCursor();
                    groupCursor.setId(lastExternal.getId());
                    groupCursor.setGroupValue(enumValue.name());
                    nextCursor = groupCursor.encode();
                }
            }

            // 빈 그룹도 포함하여 일관된 응답 구조 유지
            List<ExternalGroupedResponseDTO.ExternalItemDTO> externalDTOs = groupExternals.stream()
                    .map(ExternalConverter::toExternalItemDTO)
                    .toList();

            ExternalGroupedResponseDTO.FilteredExternalGroup group = ExternalGroupedResponseDTO.FilteredExternalGroup.builder()
                    .filterName(getDisplayName(enumValue))
                    .dataCnt(externalDTOs.size())
                    .externals(externalDTOs)
                    .build();

            groups.add(group);
            totalFetched += groupExternals.size();

            log.info("Group: {}, fetched: {}, totalFetched: {}",
                    enumValue.name(), groupExternals.size(), totalFetched);
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
        // ID 기반 커서: 커서 ID보다 큰 ID의 데이터를 조회
        return external.id.gt(cursor.getId());
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

    private boolean isSameAssigneeGroup(Long assigneeId, ExternalCursor cursor) {
        if (cursor.getGroupValue() == null) return false;

        String currentAssigneeValue = assigneeId != null ? assigneeId.toString() : "NULL_GROUP";
        return cursor.getGroupValue().equals(currentAssigneeValue);
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
                                        .name(info.getNickname())
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

    private int findAssigneeStartIndex(List<AssigneeInfo> allAssignees, String cursorGroupValue) {
        if ("NULL_GROUP".equals(cursorGroupValue)) {
            // 담당자 없음 찾기 (첫 번째가 담당자 없음)
            return 0;
        } else {
            // 특정 assigneeId 찾기
            try {
                Long targetAssigneeId = Long.parseLong(cursorGroupValue);
                for (int i = 0; i < allAssignees.size(); i++) {
                    if (targetAssigneeId.equals(allAssignees.get(i).getId())) {
                        return i;
                    }
                }
            } catch (NumberFormatException e) {
                log.warn("Invalid assignee ID in cursor: {}", cursorGroupValue);
            }
        }

        // 찾지 못한 경우 처음부터 시작
        log.warn("Assignee not found for cursor: {}, starting from beginning", cursorGroupValue);
        return 0;
    }

    private static class AssigneeInfo {
        private final Long id;
        private final String name;

        public AssigneeInfo(Long id, String name) {
            this.id = id;
            this.name = name;
        }

        public Long getId() { return id; }
        public String getName() { return name; }
    }

}
