package com.example.Veco.domain.external.repository;

import com.example.Veco.domain.external.entity.External;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExternalRepository extends JpaRepository<External, Long> {
    Optional<External> findByGithubDataId(Long githubDataId);

    List<External> findByIdIn(List<Long> externalIds);

    List<External> findByTeamId(Long teamId);
}
