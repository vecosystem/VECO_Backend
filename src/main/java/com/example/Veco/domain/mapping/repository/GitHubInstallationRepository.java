package com.example.Veco.domain.mapping.repository;

import com.example.Veco.domain.mapping.GithubInstallation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface GitHubInstallationRepository extends JpaRepository<GithubInstallation, Long> {
    @Query("select gi from GithubInstallation gi join fetch gi.team t join fetch t.workSpace w where gi.installationId = :installationId")
    Optional<GithubInstallation> findByInstallationId(@Param("installationId") Long installationId);

    Optional<GithubInstallation> findByTeamId(Long teamId);

    boolean existsByTeamId(Long teamId);
}
