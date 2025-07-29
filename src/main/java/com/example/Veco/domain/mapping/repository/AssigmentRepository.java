package com.example.Veco.domain.mapping.repository;

import com.example.Veco.domain.mapping.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AssigmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByExternalId(Long externalId);

   @Modifying
   @Query("delete from Assignment a where a.external.id = :externalId")
   void deleteByExternalId(@Param("externalId") Long externalId);
}
