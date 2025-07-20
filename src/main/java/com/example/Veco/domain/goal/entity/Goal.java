package com.example.Veco.domain.goal.entity;

import com.example.Veco.domain.common.BaseEntity;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.global.enums.Priority;
import com.example.Veco.global.enums.State;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "goal")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Goal extends BaseEntity {

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

    @Column(name = "deadline_start")
    @Builder.Default
    private LocalDate deadlineStart = null;

    @Column(name = "deadline_end")
    @Builder.Default
    private LocalDate deadlineEnd = null;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

}
