package com.example.Veco.domain.goal.service.command;

import com.example.Veco.domain.assignee.entity.Assignee;
import com.example.Veco.domain.assignee.entity.AssigneeConverter;
import com.example.Veco.domain.assignee.entity.AssigneeRepository;
import com.example.Veco.domain.goal.converter.GoalConverter;
import com.example.Veco.domain.goal.dto.request.GoalReqDTO;
import com.example.Veco.domain.goal.entity.Goal;
import com.example.Veco.domain.goal.repository.GoalRepository;
import com.example.Veco.domain.issue.entity.Issue;
import com.example.Veco.domain.mapping.MemberTeam;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.domain.team.entity.TeamException;
import com.example.Veco.domain.team.entity.TeamRepository;
import com.example.Veco.global.enums.Category;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class GoalTransactionalService {

    private final TeamRepository teamRepository;
    private final GoalRepository goalRepository;
    private final AssigneeRepository assigneeRepository;

    // 목표 이름(Veco-g3) 조회, 생성
    @Transactional
    protected Long createGoal(
            Long teamId,
            GoalReqDTO.CreateGoal dto,
            List<MemberTeam> memberTeamList,
            List<Issue> issueList
    ){
        // Team 조회
        Team team = teamRepository.findTeamById(teamId).orElseThrow(() ->
                new TeamException("해당 팀을 찾을 수 없습니다."));

        // 목표 생성
        String name = team.getWorkSpace().getName()+"-g"+team.getGoalNumber();
        Goal goal = goalRepository.save(GoalConverter.toGoal(dto,team,name));

        // 목표 <-> 담당자 연결
        List<Assignee> assigneeList = new ArrayList<>();
        memberTeamList.forEach(
                value -> assigneeList.add(
                        AssigneeConverter.toAssignee(
                                value, goal.getId(), Category.GOAL
                        )
                )
        );
        assigneeRepository.saveAll(assigneeList);

        // 목표 <-> 이슈 연결


        // 목표 +1
        team.updateGoalNumber(team.getGoalNumber()+1);

        return goal.getId();
    }
}
