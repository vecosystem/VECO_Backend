package com.example.Veco.domain.assignee.repository;

import com.example.Veco.domain.assignee.entity.Assignee;
import com.example.Veco.global.enums.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AssigneeRepository extends JpaRepository<Assignee, Long> , AssigneeQueryDsl {
    Optional<List<Assignee>> findAllByTypeAndTargetId(Category type, Long targetId);

    void deleteAllByTypeAndTargetId(Category type, Long targetId);
}
