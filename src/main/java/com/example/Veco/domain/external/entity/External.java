package com.example.Veco.domain.external.entity;


import com.example.Veco.domain.common.BaseEntity;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.global.enums.ExtServiceType;
import com.example.Veco.global.enums.Priority;
import com.example.Veco.global.enums.State;
import jakarta.persistence.*;
import lombok.*;

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
    private  String title;

    @Column(name = "content")
    @Builder.Default
    private  String content = "";

    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private State state = State.NONE;

    @Column(name = "priority",  nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Priority priority =  Priority.NONE;

    @Column(name = "deadline")
    @Builder.Default
    private LocalDate deadline = null;

    @Column(name = "service_type", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ExtServiceType type = ExtServiceType.NONE;

    @Column(name = "external_number", nullable = false)
    private Integer external_number;

    // 연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;
}
