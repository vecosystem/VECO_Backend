package com.example.Veco.domain.mapping;


import com.example.Veco.domain.common.BaseEntity;
import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.global.enums.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "member_team")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberTeam extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

    // 연관 관계
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;
}
