package com.example.Veco.domain.workspace.entity;


import com.example.Veco.domain.common.BaseEntity;
import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.team.entity.Team;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class WorkSpace extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "profile_url")
    private String profileUrl;

    @Column(name = "workspace_url")
    private String workspaceUrl;

    @Column(name = "cert_pwd")
    private String certPwd;

    private String invitePassword;
    private String inviteUrl;
    private String slug;

    @OneToMany(mappedBy = "workSpace")
    @Builder.Default
    private List<Member> members = new ArrayList<>();

    @OneToMany(mappedBy = "workSpace", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Team> teams = new ArrayList<>();
}
