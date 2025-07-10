package com.example.Veco.domain.external.entity;


import com.example.Veco.domain.common.BaseEntity;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.domain.goal.entity.Goal;
import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.global.enums.ExtServiceType;
import com.example.Veco.global.enums.Priority;
import com.example.Veco.global.enums.State;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Entity
@Table(name = "external")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class External extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description")
    @Builder.Default
    private String description = "";

    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private State state = State.NONE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "priority", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Priority priority = Priority.NONE;

    @Column(name = "deadline")
    @Builder.Default
    private LocalDate deadline = null;

    @Column(name = "service_type", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ExtServiceType type = ExtServiceType.NONE;

    @Column(name = "external_code", nullable = false)
    private String external_code;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id")
    private Goal goal;
    // 연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;
}
