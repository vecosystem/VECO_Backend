package com.example.Veco.domain.team.entity;


import com.example.Veco.domain.common.BaseEntity;
import com.example.Veco.domain.mapping.GithubInstallation;
import com.example.Veco.domain.workspace.entity.WorkSpace;
import jakarta.persistence.*;
import lombok.*;

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

    // 연관 관계
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "workspace_id")
    private WorkSpace workSpace;

    @OneToOne(mappedBy = "team")
    private GithubInstallation githubInstallation;

    // update
    public void updateGoalNumber(Long goalNumber){ this.goalNumber = goalNumber; }
}
