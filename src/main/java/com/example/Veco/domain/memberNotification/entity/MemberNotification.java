package com.example.Veco.domain.memberNotification.entity;

import com.example.Veco.domain.common.BaseEntity;
import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.notification.entity.Notification;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberNotification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    @Setter(AccessLevel.PRIVATE)
    private Boolean isRead = Boolean.FALSE;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "notification_id")
    private Notification notification;

    public void markAsRead() {
        this.isRead = true;
    }

}
