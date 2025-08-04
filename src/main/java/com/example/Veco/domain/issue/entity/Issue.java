package com.example.Veco.domain.issue.entity;

import com.example.Veco.domain.common.BaseEntity;
import com.example.Veco.domain.goal.entity.Goal;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.global.enums.Priority;
import com.example.Veco.global.enums.State;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "issue")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Issue extends BaseEntity {

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

    // 이슈가 속한 팀
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    // 연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id")
    private Goal goal;

    // update
    public void updateGoal(Goal goal) { this.goal = goal; }
    public void updateTitle(String title) { this.title = title; }
    public void updateContent(String content) { this.content = content; }
    public void updateState(State state) { this.state = state; }
    public void updatePriority(Priority priority) { this.priority = priority; }
    public void updateDeadlineStart(LocalDate deadlineStart) { this.deadlineStart = deadlineStart; }
    public void updateDeadlineEnd(LocalDate deadlineEnd) { this.deadlineEnd = deadlineEnd; }

}
