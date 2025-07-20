package com.example.Veco.domain.notification.repository;

import com.example.Veco.domain.notification.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface NotiRepository extends JpaRepository<Notification, Long>, NotiQueryDsl {

}
