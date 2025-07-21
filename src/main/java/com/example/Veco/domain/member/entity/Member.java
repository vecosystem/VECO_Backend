package com.example.Veco.domain.member.entity;


import com.example.Veco.domain.common.BaseEntity;
import com.example.Veco.domain.member.enums.Provider;
import com.example.Veco.domain.profile.entity.Profile;
import com.example.Veco.domain.workspace.entity.WorkSpace;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "member")
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    private String email;

    @Column(name = "provider")
    @Enumerated(EnumType.STRING)
    private Provider provider;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    private WorkSpace workSpace;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;
}
