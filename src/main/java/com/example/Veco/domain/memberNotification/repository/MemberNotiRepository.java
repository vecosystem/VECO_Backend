package com.example.Veco.domain.memberNotification.repository;

import com.example.Veco.domain.memberNotification.entity.MemberNotification;
import org.springframework.data.jpa.repository.JpaRepository;


public interface MemberNotiRepository extends JpaRepository<MemberNotification, Long>, MemberNotiQueryDsl {

}
