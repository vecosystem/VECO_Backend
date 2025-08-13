package com.example.Veco.domain.team.repository;

import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.domain.workspace.entity.WorkSpace;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findTeamById(Long id);

    Page<Team> findAllByWorkSpace(WorkSpace workspace, Pageable pageable);

    Team findFirstByWorkSpaceOrderById(WorkSpace workSpace);

    int countByWorkSpace(WorkSpace workSpace);

    boolean existsByName(String name);

    boolean existsByNameAndWorkSpace(String name, WorkSpace workSpace);

    @Query("select min(t.id) from Team t where t.workSpace = :workspace")
    Long findMinTeamIdByWorkSpace(@Param("workspace") WorkSpace workspace);

    @Query("select max(t.order) from Team t where t.workSpace = :workspace")
    Optional<Integer> findMaxOrderByWorkSpace(@Param("workspace") WorkSpace workSpace);
}
