package com.example.Veco.domain.notification.entity;

import com.example.Veco.domain.common.BaseEntity;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.global.enums.Category;
import jakarta.persistence.*;
import lombok.*;

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

    /* @Column(name = "content")
    private String content; */

    @Column(name = "type" , nullable = false)
    @Enumerated(EnumType.STRING)
    private Category type;

    @Column(name = "type_id" , nullable = false)
    private Long typeId;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "team_id")
    private Team team;

}
