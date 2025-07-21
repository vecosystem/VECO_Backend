package com.example.Veco.domain.goal.service.query;

import com.example.Veco.domain.assignee.entity.Assignee;
import com.example.Veco.domain.assignee.repository.AssigneeRepository;
import com.example.Veco.domain.comment.entity.Comment;
import com.example.Veco.domain.comment.repository.CommentRepository;
import com.example.Veco.domain.comment.entity.CommentRoom;
import com.example.Veco.domain.goal.entity.QGoal;
import com.example.Veco.domain.mapping.repository.CommentRoomRepository;
import com.example.Veco.domain.goal.converter.GoalConverter;
import com.example.Veco.domain.goal.dto.response.GoalResDTO;
import com.example.Veco.domain.goal.dto.response.GoalResDTO.Data;
import com.example.Veco.domain.goal.dto.response.GoalResDTO.FilteringGoal;
import com.example.Veco.domain.goal.dto.response.GoalResDTO.FullGoal;
import com.example.Veco.domain.goal.dto.response.GoalResDTO.Pageable;
import com.example.Veco.domain.goal.dto.response.GoalResDTO.SimpleGoal;
import com.example.Veco.domain.goal.dto.response.GoalResDTO.Teammate;
import com.example.Veco.domain.goal.entity.Goal;
import com.example.Veco.domain.goal.exception.GoalException;
import com.example.Veco.domain.goal.exception.code.GoalErrorCode;
import com.example.Veco.domain.goal.repository.GoalRepository;
import com.example.Veco.domain.issue.entity.Issue;
import com.example.Veco.domain.issue.repository.IssueRepository;
import com.example.Veco.domain.mapping.entity.MemberTeam;
import com.example.Veco.domain.mapping.repository.MemberTeamRepository;
import com.example.Veco.global.enums.Category;
import com.example.Veco.global.enums.Priority;
import com.example.Veco.global.enums.State;
import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class GoalQueryService {

    // 리포지토리
    private final GoalRepository goalRepository;
    private final MemberTeamRepository memberTeamRepository;
    private final AssigneeRepository assigneeRepository;
    private final IssueRepository issueRepository;
    private final CommentRepository commentRepository;
    private final CommentRoomRepository commentRoomRepository;

    // 팀 내 모든 목표 조회
    public Pageable<FilteringGoal<SimpleGoal>> getGoals(
            Long teamId,
            String cursor,
            Integer size,
            String query
    ) {
        // 커서 기반 페이지네이션(다음 데이터 존재하면 해당 목표 ID가 다음 커서)
        // 조회 후 query를 통해 필터별 데이터 정렬

        // 객체 생성
        QGoal goal = QGoal.goal;

        // 조건 설정: 목표의 Team = teamID
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(goal.team.id.eq(teamId));

        // 커서 추출: -1이면 X
        if (!cursor.equals("-1")){
            try {
                builder.and(goal.id.loe(Long.parseLong(cursor)));
            } catch (NumberFormatException ex) {
                throw new GoalException(GoalErrorCode.CURSOR_INVALID);
            }
        }

        // 데이터 조회
        List<SimpleGoal> result = goalRepository.findGoalsByTeamId(builder, size);

        // 페이지네이션 메타데이터 설정
        boolean hasNext = result.size() > size;
        int pageSize = Math.min(result.size(), size);
        String nextCursor = hasNext ? result.get(pageSize).id().toString() : result.get(pageSize-1).id().toString();

        // 조회한 데이터 사이즈 조절
        if (hasNext) {
            result = result.subList(0, size);
        }

        // 필터별 분류
        // 상태: 없음 → 진행중 → 해야할 일 → 완료 → 검토 → 삭제
        // 우선순위: 없음 → 긴급 → 높음 → 보통 → 낮음
        // 담당자: 사전순(프론트에서 처리)
        List<FilteringGoal<SimpleGoal>> filterResult = new ArrayList<>();
        switch (query.toLowerCase()) {
            case "state": {
                // 필터 설정
                for (State filter : State.values()){
                    // 필터링에 맞는 모든 목표 개수 조회
                    builder =  new BooleanBuilder();
                    builder.and(goal.state.eq(filter))
                            .and(goal.team.id.eq(teamId));
                    Long dataCnt = goalRepository.findGoalsCountByFilter(builder);
                    List<SimpleGoal> temp = new ArrayList<>();
                    // 순서별 데이터 분류: O(6N) = O(N)
                    result.forEach(
                            value -> {
                                if (value.state().equals(filter)){
                                    temp.add(value);
                                }
                            }
                    );
                    // 분류한 데이터 filterResult 삽입
                    filterResult.add(GoalConverter.toFilteringGoal(temp, filter.name(), Math.toIntExact(dataCnt)));
                }
                break;
            }
            case "priority": {
                // 필터 설정
                for (Priority filter : Priority.values()){
                    // 필터링에 맞는 모든 목표 개수 조회
                    builder =  new BooleanBuilder();
                    builder.and(goal.priority.eq(filter))
                            .and(goal.team.id.eq(teamId));
                    Long dataCnt = goalRepository.findGoalsCountByFilter(builder);
                    List<SimpleGoal> temp = new ArrayList<>();
                    // 순서별 데이터 분류: O(5N) = O(N)
                    result.forEach(
                            value -> {
                                if (value.priority().equals(filter)){
                                    temp.add(value);
                                }
                            }
                    );
                    // 분류한 데이터 filterResult 삽입
                    filterResult.add(GoalConverter.toFilteringGoal(temp, filter.name(), Math.toIntExact(dataCnt)));
                }
                break;
            }
            case "manager": {
                // 담당자 리스트 뽑아와서 Map 처리: 담당자 이름 : 개수
                List<String> managers = goalRepository.findGoalsAssigneeInTeam(teamId);
                Map<String, Integer> map = new HashMap<>();
                for (String name : managers){
                    if (map.containsKey(name)){
                        map.put(name, map.get(name) + 1);
                    } else {
                        map.put(name, 1);
                    }
                }

                // 담당자 별 데이터 분류
                for (String filter : map.keySet().stream().sorted().toList()){
                    List<SimpleGoal> temp = new ArrayList<>();
                    // 순서별 데이터 분류: O(NM) N:result, M:managerInfo
                    result.forEach(
                            value -> {
                                if (value.managers().getInfo().stream()
                                        .anyMatch(dto -> dto.name().equals(filter))
                                ){
                                    temp.add(value);
                                }
                            }
                    );
                    // 분류한 데이터 filterResult 삽입
                    filterResult.add(GoalConverter.toFilteringGoal(temp, filter, map.get(filter)));
                }
                break;
            }

            default: {
                throw new GoalException(GoalErrorCode.QUERY_INVALID);
            }
        }

        // 조회한 데이터들 DTO 포장
        return GoalConverter.toPageable(filterResult, hasNext, nextCursor, pageSize);
    }

    // 목표 간단 조회
    public Data<GoalResDTO.GoalInfo> getSimpleGoal(
            Long teamId
    ) {
        // 목표 조회
        List<Goal> goals = goalRepository.findAllByTeamId(teamId);
        if (goals.isEmpty()){
            throw new GoalException(GoalErrorCode.NOT_FOUND_IN_TEAM);
        }

        // DTO 변환
        return GoalConverter.toData(goals.stream().map(GoalConverter::toGoalInfo).toList());
    }

    // 목표 상세 조회
    public FullGoal getGoalDetail(
            Long goalId
    ){

        // 필요한 요소: 목표, 담당자, 이슈, 댓글
        // 목표 조회
        Goal goal = goalRepository.findById(goalId).orElseThrow(() ->
                new GoalException(GoalErrorCode.NOT_FOUND));

        // 담당자 조회: 없으면 []
        List<Assignee> assignees = assigneeRepository.findAllByTypeAndTargetId(Category.GOAL, goalId)
                .orElse(new ArrayList<>());

        // 이슈 조회: 없으면 []
        List<Issue> issues = issueRepository.findAllByGoal(goal)
                .orElse(new ArrayList<>());

        // 댓글 조회(댓글방 조회 -> 댓글 조회, 댓글 최신순): 없으면 []
        CommentRoom commentRooms = commentRoomRepository.findByRoomTypeAndTargetId(Category.GOAL, goalId);
        List<Comment> comments = commentRepository.findAllByCommentRoomOrderByIdDesc(commentRooms)
                .orElse(new ArrayList<>());

        // 조회한 요소들 DTO 변환
        return GoalConverter.toFullGoal(
                goal,
                issues.stream().map(GoalConverter::toIssueInfo).toList(),
                assignees.stream().map(GoalConverter::toManagerInfo).toList(),
                comments.stream().map(GoalConverter::toCommentInfo).toList()
        );
    }

    // 팀원 조회
    public Data<Teammate> getTeammate(
            Long teamId
    ){
        // 팀원 조회
        List<MemberTeam> memberTeam = memberTeamRepository.findAllByTeamId(teamId);

        // 존재하면 DTO 담아 반환
        if (!memberTeam.isEmpty()){
            // 조회한 팀원 DTO 변환
            List<Teammate> teammateList = memberTeam.stream()
                    .map(GoalConverter::toTeammate)
                    .toList();
            // 응답 DTO 변환
            return GoalConverter.toData(teammateList);
        } else {
            return null;
        }
    }
}
