package com.example.Veco.domain.team.entity;


import com.example.Veco.domain.common.BaseEntity;
import com.example.Veco.domain.mapping.entity.MemberTeam;
import com.example.Veco.domain.workspace.entity.WorkSpace;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "team")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Team extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "profile_url")
    private String profileUrl;

    @Column(name = "goal_number")
    private Long goalNumber;

    @Column(name = "team_order")
    private int order;

    // 연관 관계
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "workspace_id")
    private WorkSpace workSpace;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberTeam> memberTeams = new ArrayList<>();

    // update
    public void updateGoalNumber(Long goalNumber){ this.goalNumber = goalNumber; }

    public void updateOrder(int order){ this.order = order; }
}
