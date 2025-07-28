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
    private Boolean isRead = Boolean.FALSE;

    @Column(name = "is_deleted", nullable = false)
    @Builder.Default
    private Boolean isDeleted = Boolean.FALSE;   // 삭제한 알림 재생성 방지

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "notification_id")
    private Notification notification;

    public void markAsRead() {
        this.isRead = true;
    }

    public void markAsDeleted() {
        this.isDeleted = true;
    }

}
