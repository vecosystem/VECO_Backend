package com.example.Veco.domain.workspace.repository;

import com.example.Veco.domain.workspace.entity.WorkSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkspaceRepository extends JpaRepository<WorkSpace, Long> {

    boolean existsBySlug(String slug);
}
