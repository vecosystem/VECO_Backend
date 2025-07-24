package com.example.Veco.domain.assignee.converter;

import com.example.Veco.domain.assignee.entity.Assignee;
import com.example.Veco.domain.mapping.entity.MemberTeam;
import com.example.Veco.global.enums.Category;

public class AssigneeConverter {

    // MemberTeam, id -> Assignee
    public static Assignee toAssignee(
            MemberTeam memberTeam,
            Long targetId,
            Category type
    ){
        return Assignee.builder()
                .targetId(targetId)
                .type(type)
                .memberTeam(memberTeam)
                .build();
    }
}
