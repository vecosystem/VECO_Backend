package com.example.Veco.domain.issue.service;

import com.example.Veco.domain.assignee.entity.Assignee;
import com.example.Veco.domain.assignee.repository.AssigneeRepository;
import com.example.Veco.domain.comment.entity.Comment;
import com.example.Veco.domain.comment.entity.CommentRoom;
import com.example.Veco.domain.comment.repository.CommentRepository;
import com.example.Veco.domain.goal.entity.Goal;
import com.example.Veco.domain.goal.exception.GoalException;
import com.example.Veco.domain.goal.exception.code.GoalErrorCode;
import com.example.Veco.domain.goal.repository.GoalRepository;
import com.example.Veco.domain.issue.converter.IssueConverter;
import com.example.Veco.domain.issue.dto.IssueResponseDTO;
import com.example.Veco.domain.issue.dto.IssueResponseDTO.SimpleIssue;
import com.example.Veco.domain.issue.entity.Issue;
import com.example.Veco.domain.issue.entity.QIssue;
import com.example.Veco.domain.issue.exception.IssueException;
import com.example.Veco.domain.issue.exception.code.IssueErrorCode;
import com.example.Veco.domain.issue.repository.IssueRepository;
import com.example.Veco.domain.mapping.repository.CommentRoomRepository;
import com.example.Veco.global.enums.Category;
import com.example.Veco.global.enums.Priority;
import com.example.Veco.global.enums.State;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

    public IssueResponseDTO.Pageable<IssueResponseDTO.FilteringIssue<IssueResponseDTO.IssueWithManagers>> getIssuesByTeamId(
            Long teamId,
            String cursor,
            Integer size,
            String query
    ) {

        QIssue qIssue = QIssue.issue;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qIssue.team.id.eq(teamId));

        if (!cursor.equals("-1")) {
            // 커서가 -1이 아니면 해당 커서 이후의 데이터 조회
            try {
                builder.and(qIssue.id.loe(Long.parseLong(cursor)));
            } catch (NumberFormatException ex) {
                throw new IssueException(IssueErrorCode.CURSOR_INVALID);
            }
        }

        // 데이터 조회
        List<SimpleIssue> result = issueRepository.findIssuesByTeamId(builder, size);

        // 페이지네이션 메타데이터 설정
        boolean hasNext = result.size() > size;
        int pageSize = Math.min(result.size(), size);
        String nextCursor = hasNext ? result.get(pageSize).id().toString() : result.get(pageSize - 1).id().toString();

        // 조회한 데이터 사이즈 조절
        if (hasNext) {
            result = result.subList(0, size);
        }

        // 필터별 분류
        // 상태: 없음 → 진행중 → 해야할 일 → 완료 → 검토 → 삭제
        // 우선순위: 없음 → 긴급 → 높음 → 보통 → 낮음
        // 담당자: 사전순(프론트에서 처리)
        List<IssueResponseDTO.FilteringIssue<IssueResponseDTO.IssueWithManagers>> filterResult = new ArrayList<>();
        Map<Long, List<Assignee>> assignees = issueRepository.findManagerInfoByTeamId(teamId);
        List<IssueResponseDTO.IssueWithManagers> issueWithManagers = new ArrayList<>();

        result.forEach(issue -> {
            List<Assignee> issueManagers = assignees.getOrDefault(issue.id(), Collections.emptyList());

            issueWithManagers.add(IssueConverter.toIssueWithManagers(issue, IssueConverter.toSimpleManagerInfos(issueManagers)));
        });

        switch (query.toLowerCase()) {
            case "state": {
                // 필터 설정
                for (State filter : State.values()) {
                    // 필터링에 맞는 모든 목표 개수 조회
                    builder = new BooleanBuilder();
                    builder.and(qIssue.state.eq(filter))
                            .and(qIssue.team.id.eq(teamId));
                    Long dataCnt = issueRepository.findIssuesCountByFilter(builder);
                    List<IssueResponseDTO.IssueWithManagers> temp = new ArrayList<>();
                    // 순서별 데이터 분류: O(6N) = O(N)
                    issueWithManagers.forEach(
                            value -> {
                                if (value.getState().equals(filter)) {
                                    temp.add(value);
                                }
                            }
                    );
                    // 분류한 데이터 filterResult 삽입
                    filterResult.add(IssueConverter.toFilteringIssue(temp, filter.name(), Math.toIntExact(dataCnt)));
                }
                break;
            }
            case "priority": {
                // 필터 설정
                for (Priority filter : Priority.values()) {
                    // 필터링에 맞는 모든 목표 개수 조회
                    builder = new BooleanBuilder();
                    builder.and(qIssue.priority.eq(filter))
                            .and(qIssue.team.id.eq(teamId));
                    Long dataCnt = issueRepository.findIssuesCountByFilter(builder);
                    List<IssueResponseDTO.IssueWithManagers> temp = new ArrayList<>();
                    // 순서별 데이터 분류: O(5N) = O(N)
                    issueWithManagers.forEach(
                            value -> {
                                if (value.getPriority().equals(filter)) {
                                    temp.add(value);
                                }
                            }
                    );
                    // 분류한 데이터 filterResult 삽입
                    filterResult.add(IssueConverter.toFilteringIssue(temp, filter.name(), Math.toIntExact(dataCnt)));
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
                map.put("담당자 없음", 0);

                // 담당자 별 데이터 분류
                for (String filter : map.keySet().stream().sorted().toList()) {
                    List<IssueResponseDTO.IssueWithManagers> temp = new ArrayList<>();
                    issueWithManagers.forEach(
                            value -> {
                                if (value.getManagers().info().stream()
                                        .anyMatch(dto -> dto.name().equals(filter))
                                ) {
                                    temp.add(value);
                                } else if (value.getManagers().cnt() == 0 && filter.equals("담당자 없음")) {
                                    // 담당자가 없는 경우
                                    temp.add(value);
                                    map.put("담당자 없음", map.get("담당자 없음") + 1);
                                }
                            }
                    );

                    // 분류한 데이터 filterResult 삽입
                    filterResult.add(IssueConverter.toFilteringIssue(temp, filter, map.get(filter)));
                }
                break;
            }
            case "goal": {
                // 목표 리스트 뽑아와서 Map 처리: 목표 : 개수
                List<IssueResponseDTO.GoalInfo> goals = issueRepository.findGoalInfoByTeamId(teamId);
                Map<IssueResponseDTO.GoalInfo, Integer> map = new HashMap<>();
                for (IssueResponseDTO.GoalInfo goal : goals) {
                    if (map.containsKey(goal)) {
                        map.put(goal, map.get(goal) + 1);
                    } else {
                        map.put(goal, 1);
                    }
                }

                // 목표 별 데이터 분류
                for (IssueResponseDTO.GoalInfo filter : map.keySet().stream().sorted(Comparator.comparing(IssueResponseDTO.GoalInfo::id)).toList()) {
                    List<IssueResponseDTO.IssueWithManagers> temp = new ArrayList<>();
                    issueWithManagers.forEach(
                            value -> {
                                if (value.getGoal().equals(filter)) {
                                    temp.add(value);
                                }
                            }
                    );
                    // 분류한 데이터 filterResult 삽입
                    filterResult.add(IssueConverter.toFilteringIssue(temp, filter.title(), map.get(filter)));
                }
                break;
            }

            default: {
                throw new GoalException(GoalErrorCode.QUERY_INVALID);
            }
        }

        return IssueConverter.toPageable(filterResult, hasNext, nextCursor, pageSize);
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
}
