package com.example.Veco.domain.goal.service.query;

import com.example.Veco.domain.assignee.entity.Assignee;
import com.example.Veco.domain.assignee.entity.QAssignee;
import com.example.Veco.domain.assignee.repository.AssigneeRepository;
import com.example.Veco.domain.comment.entity.Comment;
import com.example.Veco.domain.comment.entity.CommentRoom;
import com.example.Veco.domain.comment.repository.CommentRepository;
import com.example.Veco.domain.goal.converter.GoalConverter;
import com.example.Veco.domain.goal.dto.response.GoalResDTO;
import com.example.Veco.domain.goal.dto.response.GoalResDTO.*;
import com.example.Veco.domain.goal.entity.Goal;
import com.example.Veco.domain.goal.entity.QGoal;
import com.example.Veco.domain.goal.exception.GoalException;
import com.example.Veco.domain.goal.exception.code.GoalErrorCode;
import com.example.Veco.domain.goal.repository.GoalRepository;
import com.example.Veco.domain.issue.entity.Issue;
import com.example.Veco.domain.issue.repository.IssueRepository;
import com.example.Veco.domain.mapping.entity.MemberTeam;
import com.example.Veco.domain.mapping.repository.CommentRoomRepository;
import com.example.Veco.domain.mapping.repository.MemberTeamRepository;
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

import java.util.*;
import java.util.regex.PatternSyntaxException;

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
    private final TeamRepository teamRepository;

    // 팀 내 모든 목표 조회
    public Pageable<FilteringGoal<SimpleGoal>> getGoals(
            Long teamId,
            String cursor,
            Integer size,
            String query
    ) {
        // 커서 기반 페이지네이션(다음 데이터 존재하면 해당 목표 ID가 다음 커서)
        // 필터별 우선순위대로 조회 후 포장: 커서 = XXXX:ID

        // 객체 생성
        QGoal goal = QGoal.goal;
        QAssignee assignee = QAssignee.assignee;

        // 조건 설정: 목표의 Team = teamID
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(goal.team.id.eq(teamId));

        // 커서 추출: -1이면 X, 형식에 맞지 않으면 오류
        String firstCursor = "";
        long idCursor;
        if (!cursor.equals("-1")){
            try {
                // 커서 분리
                firstCursor = cursor.split(":")[0];
                idCursor = Long.parseLong(cursor.split(":")[1]);

                // 조건 설정
                builder.and(goal.id.loe(idCursor));

                // firstCursor 검증: 속성과 일치하지 않으면 NONE 설정
                // 담당자는 어떻게 처리하면 좋을지 논의
                switch (query.toLowerCase()) {
                    case "state": {
                        String finalFirstCursor = firstCursor;
                        if (Arrays.stream(State.values()).noneMatch(
                                state -> state.name().equals(finalFirstCursor))
                        ){
                            firstCursor = State.NONE.name();
                        }
                        break;
                    }
                    case "priority": {
                        String finalFirstCursor1 = firstCursor;
                        if (Arrays.stream(Priority.values()).noneMatch(
                                priority -> priority.name().equals(finalFirstCursor1))
                        ){
                            firstCursor = Priority.NONE.name();
                        }
                        break;
                    }
                }
            } catch (NumberFormatException | PatternSyntaxException ex) {
                throw new GoalException(GoalErrorCode.CURSOR_INVALID);
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

        // 필터별 분류
        // 상태: 없음 → 진행중 → 해야할 일 → 완료 → 검토 → 삭제
        // 우선순위: 없음 → 긴급 → 높음 → 보통 → 낮음
        // 담당자: 사전순
        List<FilteringGoal<SimpleGoal>> filterResult = new ArrayList<>();
        // 요청 데이터 수 +1 많게 조회: 메타데이터 설정용
        int cnt = 0;
        boolean isContinue = false;
        BooleanBuilder dataQuery =  new BooleanBuilder();
        dataQuery.and(goal.team.id.eq(teamId));

        // 페이지네이션 메타데이터 설정
        boolean hasNext = false;
        int pageSize = 0;
        String nextCursor = "";
        switch (query.toLowerCase()) {
            case "state": {
                // 필터 설정
                for (State filter : State.values()){
                    List<SimpleGoal> result = new ArrayList<>();

                    // 해당 필터 총 데이터 수 조회
                    dataQuery.and(goal.state.eq(filter));
                    Long dataCnt = goalRepository.findGoalsCountByFilter(dataQuery);

                    // firstCursor가 일치할때, 그떄 조회 시작
                    if ((cnt <= size) && (filter.name().equals(firstCursor) || isContinue)){
                        builder.and(goal.state.eq(filter));

                        result = goalRepository.findGoalsByTeamId(builder, size-cnt+1);

                        // 조회 시작했을때, 설정한 사이즈를 넘을때까지 조회
                        isContinue = true;
                        cnt += result.size();

                        // ID 조건 초기화
                        builder = new BooleanBuilder();
                        builder.and(goal.team.id.eq(teamId));
                    }

                    // 사이즈를 넘어 조회한 경우: 다음 데이터 존재 -> 메타데이터로 설정
                    if (cnt > size && isContinue){
                        // 그만 조회
                        isContinue = false;

                        hasNext = true;
                        pageSize = cnt-1;
                        nextCursor = filter + ":" + result.getLast().id();

                        // 사이즈 조절
                        result = result.subList(0, result.size()-1);
                    } else if (isContinue && !result.isEmpty()) {
                        pageSize = cnt;
                        nextCursor = filter + ":" + result.getLast().id();
                    }

                    // 분류한 데이터 filterResult 삽입
                    filterResult.add(GoalConverter.toFilteringGoal(result, filter.name(), Math.toIntExact(dataCnt)));

                    // 조건 초기화
                    dataQuery = new BooleanBuilder();
                    dataQuery.and(goal.team.id.eq(teamId));
                }
                break;
            }
            case "priority": {
                // 필터 설정
                for (Priority filter : Priority.values()){
                    List<SimpleGoal> result = new ArrayList<>();

                    // 해당 필터 총 데이터 수 조회
                    dataQuery.and(goal.priority.eq(filter));
                    Long dataCnt = goalRepository.findGoalsCountByFilter(dataQuery);

                    // firstCursor가 일치할때, 그떄 조회 시작
                    if ((cnt <= size) && (filter.name().equals(firstCursor) || isContinue)){
                        builder.and(goal.priority.eq(filter));

                        result = goalRepository.findGoalsByTeamId(builder, size-cnt+1);

                        // 조회 시작했을때, 설정한 사이즈를 넘을때까지 조회
                        isContinue = true;
                        cnt += result.size();

                        // ID 조건 초기화
                        builder = new BooleanBuilder();
                        builder.and(goal.team.id.eq(teamId));
                    }

                    // 사이즈를 넘어 조회한 경우: 다음 데이터 존재 -> 메타데이터로 설정
                    if (cnt > size && isContinue){
                        // 그만 조회
                        isContinue = false;

                        hasNext = true;
                        pageSize = cnt-1;
                        nextCursor = filter + ":" + result.getLast().id();

                        // 사이즈 조절
                        result = result.subList(0, result.size()-1);
                    } else if (isContinue) {
                        pageSize = cnt;
                        nextCursor = filter + ":" + result.getLast().id();
                    }

                    // 분류한 데이터 filterResult 삽입
                    filterResult.add(GoalConverter.toFilteringGoal(result, filter.name(), Math.toIntExact(dataCnt)));

                    // 조건 초기화
                    dataQuery = new BooleanBuilder();
                    dataQuery.and(goal.team.id.eq(teamId));
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

                // firstCursor가 담당자 리스트에 포함되어있는지 확인
                if (!managers.contains(firstCursor)){
                    // 없으면 사전순 처음으로 설정
                    firstCursor = managers.stream().sorted().toList().getFirst();
                }

                // 담당자 별 데이터 분류
                for (String filter : map.keySet().stream().sorted().toList()){
                    List<SimpleGoal> result = new ArrayList<>();

                    // firstCursor가 일치할때, 그떄 조회 시작
                    if ((cnt <= size) && (filter.equals(firstCursor) || isContinue)){
                        builder.and(assignee.memberTeam.member.name.eq(filter));

                        result = goalRepository.findGoalsByTeamId(builder, size-cnt+1);

                        // 조회 시작했을때, 설정한 사이즈를 넘을때까지 조회
                        isContinue = true;
                        cnt += result.size();

                        // ID 조건 초기화
                        builder = new BooleanBuilder();
                        builder.and(goal.team.id.eq(teamId));
                    }

                    // 사이즈를 넘어 조회한 경우: 다음 데이터 존재 -> 메타데이터로 설정
                    if (cnt > size && isContinue){
                        // 그만 조회
                        isContinue = false;

                        hasNext = true;
                        pageSize = cnt-1;
                        nextCursor = filter + ":" + result.getLast().id();

                        // 사이즈 조절
                        result = result.subList(0, result.size()-1);
                    } else if (isContinue) {
                        pageSize = cnt;
                        nextCursor = filter + ":" + result.getLast().id();
                    }

                    // 분류한 데이터 filterResult 삽입
                    filterResult.add(GoalConverter.toFilteringGoal(result, filter, map.get(filter)));
                }
                break;
            }

            default: {
                throw new GoalException(GoalErrorCode.QUERY_INVALID);
            }
        }

        // 데이터들이 없는 경우
        if (filterResult.stream().allMatch(
                value -> value.dataCnt().equals(0))
        ){
            return null;
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

    // 생성될 목표 이름 조회
    public String getGoalName(
            Long teamId
    ) {
        // 현재 목표 숫자 조회
        Team team = teamRepository.findById(teamId).orElseThrow(()->
                new TeamException(TeamErrorCode._NOT_FOUND));
        return team.getWorkSpace().getName()+"-g"+team.getGoalNumber();
    }
}
