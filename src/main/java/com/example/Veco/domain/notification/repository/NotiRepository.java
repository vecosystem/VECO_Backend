package com.example.Veco.domain.notification.repository;

import com.example.Veco.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotiRepository extends JpaRepository<Notification, Long>, NotiQueryDsl {

}
