package com.example.Veco.domain.issue.service;

import com.example.Veco.domain.assignee.entity.Assignee;
import com.example.Veco.domain.assignee.entity.QAssignee;
import com.example.Veco.domain.assignee.repository.AssigneeRepository;
import com.example.Veco.domain.comment.entity.Comment;
import com.example.Veco.domain.comment.entity.CommentRoom;
import com.example.Veco.domain.comment.repository.CommentRepository;
import com.example.Veco.domain.goal.entity.Goal;
import com.example.Veco.domain.goal.repository.GoalRepository;
import com.example.Veco.domain.issue.converter.IssueConverter;
import com.example.Veco.domain.issue.dto.IssueResponseDTO;
import com.example.Veco.domain.issue.dto.IssueResponseDTO.FilteringIssue;
import com.example.Veco.domain.issue.dto.IssueResponseDTO.IssueWithManagers;
import com.example.Veco.domain.issue.dto.IssueResponseDTO.SimpleIssue;
import com.example.Veco.domain.issue.dto.IssueResponseDTO.Pageable;
import com.example.Veco.domain.issue.entity.Issue;
import com.example.Veco.domain.issue.entity.QIssue;
import com.example.Veco.domain.issue.exception.IssueException;
import com.example.Veco.domain.issue.exception.code.IssueErrorCode;
import com.example.Veco.domain.issue.repository.IssueRepository;
import com.example.Veco.domain.mapping.repository.CommentRoomRepository;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.domain.team.exception.TeamException;
import com.example.Veco.domain.team.exception.code.TeamErrorCode;
import com.example.Veco.domain.team.repository.TeamRepository;
import com.example.Veco.global.enums.Category;
import com.example.Veco.global.enums.Priority;
import com.example.Veco.global.enums.State;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class IssueQueryService {

    private final IssueRepository issueRepository;
    private final AssigneeRepository assigneeRepository;
    private final GoalRepository goalRepository;
    private final CommentRoomRepository commentRoomRepository;
    private final CommentRepository commentRepository;
    private final TeamRepository teamRepository;

    public Pageable<FilteringIssue<IssueWithManagers>> getIssuesByTeamId(
            Long teamId,
            String cursor,
            Integer size,
            String query
    ) {

        QIssue qIssue = QIssue.issue;
        QAssignee qAssignee = QAssignee.assignee;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qIssue.team.id.eq(teamId));

        // 커서 추출: -1이면 X, 형식에 맞지 않으면 오류
        String firstCursor = "";
        long idCursor;
        if (!cursor.equals("-1")) {
            try {
                // 커서 분리
                firstCursor = cursor.split(":")[0];
                idCursor = Long.parseLong(cursor.split(":")[1]);

                // 조건 설정
                builder.and(qIssue.id.loe(idCursor));

                // firstCursor 검증: 속성과 일치하지 않으면 NONE 설정
                switch (query.toLowerCase()) {
                    case "state": {
                        String finalFirstCursor = firstCursor;
                        if (Arrays.stream(State.values()).noneMatch(
                                state -> state.name().equals(finalFirstCursor))
                        ) {
                            firstCursor = State.NONE.name();
                        }
                        break;
                    }
                    case "priority": {
                        String finalFirstCursor1 = firstCursor;
                        if (Arrays.stream(Priority.values()).noneMatch(
                                priority -> priority.name().equals(finalFirstCursor1))
                        ) {
                            firstCursor = Priority.NONE.name();
                        }
                        break;
                    }
                }
            } catch (NumberFormatException | PatternSyntaxException ex) {
                throw new IssueException(IssueErrorCode.CURSOR_INVALID);
            }
        } else {
            // 커서가 없는 경우, 기본값 NONE 설정, 담당자는 null
            switch (query.toLowerCase()) {
                case "state": {
                    firstCursor = State.NONE.name();
                    break;
                }
                case "priority": {
                    firstCursor = Priority.NONE.name();
                    break;
                }
            }
        }

        // 요청 데이터 수 +1 많게 조회: 메타데이터 설정용
        size += 1;

        // 필터별 분류
        // 상태: 없음 → 진행중 → 해야할 일 → 완료 → 검토 → 삭제
        // 우선순위: 없음 → 긴급 → 높음 → 보통 → 낮음
        // 담당자: 사전순(프론트에서 처리)
        List<FilteringIssue<SimpleIssue>> filterResult = new ArrayList<>();
        boolean isContinue = false;
        BooleanBuilder dataQuery = new BooleanBuilder();
        dataQuery.and(qIssue.team.id.eq(teamId));

        // 페이지네이션 메타데이터 설정
        boolean hasNext = false;
        int pageSize = 0;
        String nextCursor = "";
        switch (query.toLowerCase()) {
            case "state": {
                // 필터 설정
                for (State filter : State.values()) {
                    List<SimpleIssue> result = new ArrayList<>();

                    // 해당 필터 총 데이터 수 조회
                    dataQuery.and(qIssue.state.eq(filter));
                    Long dataCnt = issueRepository.findIssuesCountByFilter(dataQuery);

                    // firstCursor가 일치할 때, 조회 시작
                    if ((size > 0) && (filter.name().equals(firstCursor) || isContinue)) {
                        builder.and(qIssue.state.eq(filter));

                        result = issueRepository.findIssuesByTeamId(builder, size);

                        // 조회 시작했을때, 설정한 사이즈를 넘을때까지 조회
                        isContinue = true;
                        size -= result.size();
                        pageSize += result.size();

                        // ID 조건 초기화
                        builder = new BooleanBuilder();
                        builder.and(qIssue.team.id.eq(teamId));
                    }

                    // 사이즈를 넘어 조회한 경우: 다음 데이터 존재 -> 메타데이터로 설정
                    if (size <= 0 && isContinue) {
                        // 그만 조회
                        isContinue = false;

                        hasNext = true;
                        pageSize -= 1;
                        nextCursor = filter + ":" + result.getLast().id();

                        // 사이즈 조절
                        if (result.size() > 1) {
                            result = result.subList(0, result.size() - 1);
                        } else {
                            result = new ArrayList<>();
                        }
                    } else if (isContinue && !result.isEmpty()) {
                        nextCursor = filter + ":" + result.getLast().id();
                    }

                    // 분류한 데이터 filterResult 삽입
                    filterResult.add(IssueConverter.toFilteringIssue(result, filter.name(), Math.toIntExact(dataCnt)));

                    // 조건 초기화
                    dataQuery = new BooleanBuilder();
                    dataQuery.and(qIssue.team.id.eq(teamId));
                }
                break;
            }
            case "priority": {
                // 필터 설정
                for (Priority filter : Priority.values()) {
                    List<SimpleIssue> result = new ArrayList<>();

                    // 해당 필터 총 데이터 수 조회
                    dataQuery.and(qIssue.priority.eq(filter));
                    Long dataCnt = issueRepository.findIssuesCountByFilter(dataQuery);

                    // firstCursor가 일치할 때, 조회 시작
                    if ((size > 0) && (filter.name().equals(firstCursor) || isContinue)) {
                        builder.and(qIssue.priority.eq(filter));

                        result = issueRepository.findIssuesByTeamId(builder, size);

                        // 조회 시작했을때, 설정한 사이즈를 넘을때까지 조회
                        isContinue = true;
                        size -= result.size();
                        pageSize += result.size();

                        // ID 조건 초기화
                        builder = new BooleanBuilder();
                        builder.and(qIssue.team.id.eq(teamId));
                    }

                    // 사이즈를 넘어 조회한 경우: 다음 데이터 존재 -> 메타데이터로 설정
                    if (size <= 0 && isContinue) {
                        // 그만 조회
                        isContinue = false;

                        hasNext = true;
                        pageSize -= 1;
                        nextCursor = filter + ":" + result.getLast().id();

                        // 사이즈 조절
                        if (result.size() > 1) {
                            result = result.subList(0, result.size() - 1);
                        } else {
                            result = new ArrayList<>();
                        }
                    } else if (isContinue && !result.isEmpty()) {
                        nextCursor = filter + ":" + result.getLast().id();
                    }

                    // 분류한 데이터 filterResult 삽입
                    filterResult.add(IssueConverter.toFilteringIssue(result, filter.name(), Math.toIntExact(dataCnt)));

                    // 조건 초기화
                    dataQuery = new BooleanBuilder();
                    dataQuery.and(qIssue.team.id.eq(teamId));
                }
                break;
            }
            case "manager": {
                // 담당자 리스트 뽑아와서 Map 처리: 담당자 이름 : 개수
                List<String> managers = issueRepository.findIssuesAssigneeInTeam(teamId);
                Map<String, Integer> map = new HashMap<>();
                for (String name : managers) {
                    if (map.containsKey(name)) {
                        map.put(name, map.get(name) + 1);
                    } else {
                        map.put(name, 1);
                    }
                }

                // 담당자가 없는 이슈의 개수 조회
                Long unassignedCount = issueRepository.findUnassignedIssuesCountByTeamId(teamId);
                map.put("담당자 없음", Math.toIntExact(unassignedCount));
                managers.add("담당자 없음");

                // firstCursor가 담당자 리스트에 포함되어있는지 확인
                if (!managers.contains(firstCursor)) {
                    // 없으면 담당자 없음부터
                    firstCursor = "담당자 없음";
                }

                // 담당자 별 데이터 분류
                for (String filter : map.keySet().stream().sorted().toList()) {
                    List<SimpleIssue> result = new ArrayList<>();

                    // firstCursor가 일치할 때, 조회 시작
                    if ((size > 0) && (filter.equals(firstCursor) || isContinue)) {
                        if (filter.equals("담당자 없음")) {
                            result = issueRepository.findUnassignedIssuesByTeamId(teamId, builder, size);
                        } else {
                            builder.and(qAssignee.memberTeam.member.name.eq(filter));
                            result = issueRepository.findIssuesByTeamId(builder, size);
                        }

                        // 조회 시작했을때, 설정한 사이즈를 넘을때까지 조회
                        isContinue = true;
                        size -= result.size();
                        pageSize += result.size();

                        // ID 조건 초기화
                        builder = new BooleanBuilder();
                        builder.and(qIssue.team.id.eq(teamId));
                    }

                    // 사이즈를 넘어 조회한 경우: 다음 데이터 존재 -> 메타데이터로 설정
                    if (size <= 0 && isContinue) {
                        // 그만 조회
                        isContinue = false;

                        hasNext = true;
                        pageSize -= 1;
                        nextCursor = filter + ":" + result.getLast().id();

                        // 사이즈 조절
                        if (result.size() > 1) {
                            result = result.subList(0, result.size() - 1);
                        } else {
                            result = new ArrayList<>();
                        }
                    } else if (isContinue && !result.isEmpty()) {
                        nextCursor = filter + ":" + result.getLast().id();
                    }

                    // 분류한 데이터 filterResult 삽입
                    filterResult.add(IssueConverter.toFilteringIssue(result, filter, map.get(filter)));
                }
                break;
            }
            case "goal": {
                // 목표 리스트 뽑아와서 Map 처리: 목표 이름 : 개수
                List<String> goals = issueRepository.findGoalsByTeamId(teamId);
                Map<String, Integer> map = new HashMap<>();
                for (String goal : goals) {
                    if (map.containsKey(goal)) {
                        map.put(goal, map.get(goal) + 1);
                    } else {
                        map.put(goal, 1);
                    }
                }

                // 목표가 없는 이슈의 개수 조회
                Long noGoalCount = issueRepository.findNoGoalIssuesCountByTeamId(teamId);
                map.put("목표 없음", Math.toIntExact(noGoalCount));

                // firstCursor가 목표 리스트에 포함되어있는지 확인
                if (!goals.contains(firstCursor)) {
                    // 없으면 목표 없음부터
                    firstCursor = "목표 없음";
                }

                // 목표 별 데이터 분류
                for (String filter : map.keySet().stream().sorted().toList()) {
                    List<SimpleIssue> result = new ArrayList<>();

                    // firstCursor가 일치할 때, 조회 시작
                    if ((size > 0) && (filter.equals(firstCursor) || isContinue)) {
                        builder.and(qIssue.team.id.eq(teamId));
                        if (filter.equals("목표 없음")) {
                            builder.and(qIssue.goal.isNull());
                        } else {
                            builder.and(qIssue.goal.title.eq(filter));
                        }
                        result = issueRepository.findIssuesByTeamId(builder, size);

                        // 조회 시작했을때, 설정한 사이즈를 넘을때까지 조회
                        isContinue = true;
                        size -= result.size();
                        pageSize += result.size();

                        // ID 조건 초기화
                        builder = new BooleanBuilder();
                        builder.and(qIssue.team.id.eq(teamId));
                    }

                    // 사이즈를 넘어 조회한 경우: 다음 데이터 존재 -> 메타데이터로 설정
                    if (size <= 0 && isContinue) {
                        // 그만 조회
                        isContinue = false;

                        hasNext = true;
                        pageSize -= 1;
                        nextCursor = filter + ":" + result.getLast().id();

                        // 사이즈 조절
                        if (result.size() > 1) {
                            result = result.subList(0, result.size() - 1);
                        } else {
                            result = new ArrayList<>();
                        }
                    } else if (isContinue && !result.isEmpty()) {
                        nextCursor = filter + ":" + result.getLast().id();
                    }

                    // 분류한 데이터 filterResult 삽입
                    filterResult.add(IssueConverter.toFilteringIssue(result, filter, map.get(filter)));
                }
                break;
            }

            default: {
                throw new IssueException(IssueErrorCode.QUERY_INVALID);
            }
        }

        // 데이터들이 없는 경우
        if (filterResult.stream().allMatch(
                value -> value.dataCnt().equals(0))
        ){
            return null;
        }

        Map<Long, List<Assignee>> assignees = issueRepository.findManagerInfoByTeamId(teamId);

        List<FilteringIssue<IssueWithManagers>> resultWithManagers = filterResult.stream()
                .map(filterGroup -> {
                    // 현재 필터 그룹의 이슈들을 IssueWithManagers로 변환
                    List<IssueWithManagers> issuesWithManagers = filterGroup.issues().stream()
                            .map(simpleIssue -> {
                                List<Assignee> issueManagers = assignees.getOrDefault(simpleIssue.id(), Collections.emptyList());
                                return IssueConverter.toIssueWithManagers(simpleIssue, IssueConverter.toSimpleManagerInfos(issueManagers));
                            })
                            .collect(Collectors.toList());
                    // 담당자 정보가 포함된 새로운 필터 그룹 생성
                    return IssueConverter.toFilteringIssue(issuesWithManagers, filterGroup.filterName(), filterGroup.dataCnt());
                })
                .collect(Collectors.toList());



        return IssueConverter.toPageable(resultWithManagers, hasNext, nextCursor, pageSize);
    }

    public IssueResponseDTO.DetailIssue getIssueDetailById(Long issueId) {
        Issue issue = issueRepository.findById(issueId).orElseThrow(() ->
                new IssueException(IssueErrorCode.NOT_FOUND));

        // 담당자 조회: 없으면 []
        List<Assignee> assignees = assigneeRepository.findAllByTypeAndTargetId(Category.ISSUE, issueId)
                .orElse(new ArrayList<>());

        // 목표 조회
        Goal goal = null;

        if (issue.getGoal() != null) {
            goal = goalRepository.findById(issue.getGoal().getId())
                    .orElse(Goal.builder()
                            .id(-1L)
                            .title("목표 없음")
                            .build()
                    );
        } else {
            goal = Goal.builder()
                    .id(-1L)
                    .title("목표 없음")
                    .build();
        }

        CommentRoom commentRooms = commentRoomRepository.findByRoomTypeAndTargetId(Category.ISSUE, issueId);
        List<Comment> comments = commentRepository.findAllByCommentRoomOrderByIdDesc(commentRooms);

        return IssueConverter.toDetailIssue(
                issue,
                IssueConverter.toSimpleManagerInfos(assignees),
                goal,
                IssueConverter.toCommentInfos(comments)
        );
    }

    public String getIssueName(
            Long teamId
    ) {
        // 현재 이슈 숫자 조회
        Team team = teamRepository.findById(teamId).orElseThrow(() ->
                new TeamException(TeamErrorCode._NOT_FOUND));

        Long issueNumber = team.getIssueNumber() != null ? team.getIssueNumber() : 1L;
        return team.getWorkSpace().getName() + "-i" + issueNumber;
    }

    public IssueResponseDTO.Data<IssueResponseDTO.IssueInfo> getSimpleIssue(
            Long teamId
    ) {
        teamRepository.findById(teamId).orElseThrow(() ->
                new TeamException(TeamErrorCode._NOT_FOUND));

        List<Issue> issues = issueRepository.findAllByTeamId(teamId);
        if (issues.isEmpty()) {
            throw new IssueException(IssueErrorCode.NOT_FOUND_IN_TEAM);
        }
        return IssueConverter.toData(issues.stream().map(IssueConverter::toIssueInfo).toList());
    }
}
