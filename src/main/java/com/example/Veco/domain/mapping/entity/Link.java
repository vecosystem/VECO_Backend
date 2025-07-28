package com.example.Veco.domain.mapping.entity;

import com.example.Veco.domain.external.entity.ExternalService;
import com.example.Veco.domain.workspace.entity.WorkSpace;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "link")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Link {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "linked_at")
    private LocalDateTime linkedAt;

    // 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "workspace_id")
    private WorkSpace workspace;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "external_service_id")
    private ExternalService externalService;

    // update
    public void updateLinkedAt(LocalDateTime linkedAt){
        this.linkedAt = linkedAt;
    }
}
