package com.example.Veco.domain.assignee.entity;


import com.example.Veco.domain.common.BaseEntity;
import com.example.Veco.domain.external.entity.External;
import com.example.Veco.domain.goal.entity.Goal;
import com.example.Veco.domain.issue.entity.Issue;
import com.example.Veco.domain.mapping.MemberTeam;
import com.example.Veco.global.enums.Category;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "assignee")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Assignee extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private Category type;

    @Column(name = "target_id")
    private Long targetId;

    // 연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "issue_id")
    private Issue issue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id")
    private Goal goal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "external_id")
    private External external;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_team_id")
    private MemberTeam memberTeam;
}
