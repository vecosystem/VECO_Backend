package com.example.Veco.domain.mapping.converter;

import com.example.Veco.domain.mapping.entity.MemberTeam;
import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.global.enums.Role;

public class MemberTeamConverter {

    // Member, Team -> MemberTeam
    public static MemberTeam toMemberTeam(
            Member member,
            Team team
    ){
        return MemberTeam.builder()
                .member(member)
                .team(team)
                .role(Role.USER)
                .build();
    }
}
