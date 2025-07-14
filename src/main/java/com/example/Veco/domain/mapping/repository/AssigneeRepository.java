package com.example.Veco.domain.mapping.repository;

import com.example.Veco.domain.mapping.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssigneeRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByExternalId(Long externalId);
}
