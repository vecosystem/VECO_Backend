package com.example.Veco.domain.notification.repository;

import com.example.Veco.domain.notification.entity.Notification;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.global.enums.Category;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface NotiQueryDsl {
    Optional<Notification> findByTargetInfo(Category type, Long typeId, Team team);
    List<Notification> findByExpireAt(LocalDate today);
}
