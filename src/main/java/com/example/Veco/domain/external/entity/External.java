package com.example.Veco.domain.external.entity;


import com.example.Veco.domain.common.BaseEntity;
import com.example.Veco.domain.external.dto.ExternalRequestDTO;
import com.example.Veco.domain.external.dto.GitHubWebhookPayload;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.domain.goal.entity.Goal;
import com.example.Veco.domain.mapping.Assignment;
import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.global.enums.ExtServiceType;
import com.example.Veco.global.enums.Priority;
import com.example.Veco.global.enums.State;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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

    @Column(name = "github_data_id")
    private Long githubDataId;

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

    @Column(name = "start_date")
    @Builder.Default
    private LocalDate startDate = null;

    @Column(name = "end_date")
    @Builder.Default
    private LocalDate endDate = null;

    @Column(name = "service_type", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ExtServiceType type = ExtServiceType.NONE;

    @Column(name = "external_code", nullable = false)
    private String externalCode;

    // 연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id")
    private Goal goal;

    @OneToMany(mappedBy = "external")
    @Builder.Default
    private List<Assignment> assignments = new ArrayList<>();

    public void setTeam(Team team) {
        this.team = team;
        team.getExternals().add(this);
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
        goal.getExternals().add(this);
    }

    public void updateExternal(ExternalRequestDTO.ExternalUpdateRequestDTO requestDTO) {
        if(requestDTO.getTitle() != null) {
            this.title = requestDTO.getTitle();
        }
        if(requestDTO.getDescription() != null) {
            this.description = requestDTO.getDescription();
        }
        if(requestDTO.getState() != null) {
            this.state = requestDTO.getState();
        }
        if (requestDTO.getStartDate() != null) {
            this.startDate = requestDTO.getStartDate();
        }
        if (requestDTO.getEndDate() != null) {
            this.endDate = requestDTO.getEndDate();
        }
        if(requestDTO.getPriority() != null) {
            this.priority = requestDTO.getPriority();
        }
    }

    public void closeIssue(){
        this.state = State.FINISH;
    }

    public void updateExternalByGithubIssue(GitHubWebhookPayload.Issue issue ){

        if(issue.getTitle() != null) {
            this.title = issue.getTitle();
        }
        if(issue.getBody() != null) {
            this.description = issue.getBody();
        }
    }

}
