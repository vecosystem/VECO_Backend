package com.example.Veco.domain.comment.entity;

import com.example.Veco.domain.common.BaseEntity;
import com.example.Veco.domain.external.entity.External;
import com.example.Veco.domain.goal.entity.Goal;
import com.example.Veco.domain.issue.entity.Issue;

import com.example.Veco.global.enums.Category;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comment_room", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"room_type", "target_id"}))
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentRoom extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_type")
    @Enumerated(EnumType.STRING)
    private Category roomType;

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

    @OneToMany(mappedBy = "commentRoom", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Comment> comments = new ArrayList<>();
}
