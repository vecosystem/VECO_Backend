package com.example.Veco.domain.notification.entity;


import com.example.Veco.domain.common.BaseEntity;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.global.enums.Category;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content")
    private String content;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private Category type;

    @Column(name = "type_id")
    private Long typeId;

    @Column(name = "is_read")
    private Boolean isRead;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt = null;

    // 연관 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;
}
