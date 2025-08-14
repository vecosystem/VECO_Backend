package com.example.Veco.domain.external.repository;

import com.example.Veco.domain.external.entity.External;
import com.example.Veco.domain.goal.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ExternalRepository extends JpaRepository<External, Long> {
    Optional<External> findByGithubDataId(Long githubDataId);

    List<External> findByIdIn(List<Long> externalIds);

    List<External> findByTeamId(Long teamId);

    @Query(value = "SELECT * FROM external WHERE deleted_at IS NOT NULL AND team_id =:teamId", nativeQuery = true)
    List<External> findAllByTeamIdAndDeleted(Long teamId);

    // 삭제된 목표 ID로 조회
    @Query(value = "SELECT * FROM external WHERE deleted_at IS NOT NULL AND id IN :externalIds", nativeQuery = true)
    List<External> findDeletedExternalsById(List<Long> externalIds);
}
