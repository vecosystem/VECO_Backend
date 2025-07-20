package com.example.Veco.domain.assignee.repository;

import com.example.Veco.domain.assignee.entity.Assignee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssigneeRepository extends JpaRepository<Assignee, Long> , AssigneeQueryDsl{

}
