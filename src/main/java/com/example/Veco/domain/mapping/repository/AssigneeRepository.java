package com.example.Veco.domain.mapping.repository;

import com.example.Veco.domain.mapping.Assignee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssigneeRepository extends JpaRepository<Assignee, Long> {
    List<Assignee> findByExternalId(Long externalId);
}
