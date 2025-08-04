package com.example.Veco.domain.assignee.converter;

import com.example.Veco.domain.assignee.entity.Assignee;
import com.example.Veco.domain.goal.entity.Goal;
import com.example.Veco.domain.issue.entity.Issue;
import com.example.Veco.domain.mapping.entity.MemberTeam;
import com.example.Veco.global.enums.Category;

public class AssigneeConverter {

    // MemberTeam, id -> Assignee
    public static Assignee toAssignee(
            MemberTeam memberTeam,
            Category type,
            Goal goal
    ){
        return Assignee.builder()
                .targetId(goal.getId())
                .type(type)
                .memberTeam(memberTeam)
                .goal(goal)
                .build();
    }

    public static Assignee toIssueAssignee(
            MemberTeam memberTeam,
            Category type,
            Issue issue
    ){
        return Assignee.builder()
                .targetId(issue.getId())
                .type(type)
                .memberTeam(memberTeam)
                .issue(issue)
                .build();
    }
}
