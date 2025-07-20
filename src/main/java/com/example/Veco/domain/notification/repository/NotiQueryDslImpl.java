package com.example.Veco.domain.notification.repository;

import com.example.Veco.domain.notification.entity.Notification;
import com.example.Veco.domain.notification.entity.QNotification;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.global.enums.Category;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class NotiQueryDslImpl implements NotiQueryDsl{
    private final JPAQueryFactory queryFactory;

    QNotification notification = QNotification.notification;

    @Override
    public Optional<Notification> findByTargetInfo(Category type, Long typeId, Team team) {
        Notification result = queryFactory
                .selectFrom(notification)
                .where(
                        notification.typeId.eq(typeId),
                        notification.type.eq(type),
                        notification.team.eq(team)
                )
                .fetchOne();

        return Optional.ofNullable(result);
    }
}
