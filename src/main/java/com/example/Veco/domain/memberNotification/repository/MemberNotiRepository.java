package com.example.Veco.domain.memberNotification.repository;

import com.example.Veco.domain.issue.entity.Issue;
import com.example.Veco.domain.memberNotification.entity.MemberNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberNotiRepository extends JpaRepository<MemberNotification, Long>, MemberNotiQueryDsl {

}
