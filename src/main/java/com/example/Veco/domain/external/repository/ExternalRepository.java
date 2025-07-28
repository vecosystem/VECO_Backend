package com.example.Veco.domain.external.repository;

import com.example.Veco.domain.external.entity.External;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExternalRepository extends JpaRepository<External, Long>{

    @Modifying
    @Query("update External e set e.state = 'DELETED' where e.id in :externalIds")
    void deleteByExternalId(@Param("externalIds") List<Long> externalIds);

    Optional<External> findByGithubDataId(Long githubDataId);
}
