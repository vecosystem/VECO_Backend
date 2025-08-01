package com.example.Veco.domain.member.entity;


import com.example.Veco.domain.common.BaseEntity;
import com.example.Veco.domain.member.enums.MemberRole;
import com.example.Veco.domain.member.enums.Provider;
import com.example.Veco.domain.profile.entity.Profile;
import com.example.Veco.domain.workspace.entity.WorkSpace;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "member")
@Getter
@Setter
@Builder(toBuilder = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    @NotNull
    private String name;

    private String nickname;

    @NotNull
    private String email;

    @Column(name = "provider")
    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Column(name = "social_uid")
    private String socialUid;

    @Column(name = "refresh_token", length = 512)
    private String refreshToken;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private MemberRole role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    private WorkSpace workSpace;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "profile_id", nullable = true)
    private Profile profile;

}
