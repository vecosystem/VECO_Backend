package com.example.Veco.domain.notification.entity;

import com.example.Veco.domain.common.BaseEntity;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.global.enums.Category;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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

    @Column(name = "type", nullable = false)
    @Enumerated(EnumType.STRING)
    private Category type;

    @Column(name = "type_id", nullable = false)
    private Long typeId;

    // 마감일 기준 1일 뒤 만료
    @Column(name = "expire_at", nullable = false)
    private LocalDate expireAt;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "team_id")
    private Team team;

}
