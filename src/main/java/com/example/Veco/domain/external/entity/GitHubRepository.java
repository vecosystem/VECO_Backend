package com.example.Veco.domain.external.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class GitHubRepository {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "repository_id")
    private Long id;


}
