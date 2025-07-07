package com.example.Veco.domain.member.entity;

import com.example.Veco.domain.common.BaseEntity;
import com.example.Veco.domain.member.enums.Provider;
import com.example.Veco.domain.workspace.entity.WorkSpace;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "member")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    private String nickname;

    private String email;

    @Column(name = "provider")
    @Enumerated(EnumType.STRING)
    private Provider provider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    private WorkSpace workSpace;
}
