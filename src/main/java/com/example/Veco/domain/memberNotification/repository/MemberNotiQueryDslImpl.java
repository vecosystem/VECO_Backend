package com.example.Veco.domain.memberNotification.repository;

import com.example.Veco.domain.memberNotification.entity.MemberNotification;
import com.example.Veco.domain.memberNotification.entity.QMemberNotification;
import com.example.Veco.domain.notification.entity.QNotification;
import com.example.Veco.global.enums.Category;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberNotiQueryDslImpl implements MemberNotiQueryDsl {

    private final JPAQueryFactory queryFactory;

    QMemberNotification memberNotification = QMemberNotification.memberNotification;
    QNotification notification = QNotification.notification;

    @Override
    public boolean existsByNotificationIdAndMemberId(Long notificationId, Long memberId) {
        Integer fetchOne = queryFactory
                .selectOne()
                .from(memberNotification)
                .where(
                        memberNotification.notification.id.eq(notificationId),
                        memberNotification.member.id.eq(memberId)
                )
                .fetchFirst();

        return fetchOne != null;
    }

    @Override
    public List<MemberNotification> findByMemberIdAndTypeAndNotDeleted(Long memberId, Category alarmType) {
        return queryFactory
                .selectFrom(memberNotification)
                .join(memberNotification.notification, notification)
                .fetchJoin()
                .where(
                        memberNotification.member.id.eq(memberId),
                        memberNotification.isDeleted.eq(false),
                        notification.type.eq(alarmType)
                )
                .orderBy(memberNotification.id.desc())
                .fetch();
    }

    @Override
    public void deleteByNotificationId(Long notiId) {
        queryFactory
                .delete(memberNotification)
                .where(memberNotification.notification.id.eq(notiId))
                .execute();
    }

}

