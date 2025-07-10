package com.example.Veco.domain.external.repository;

import com.example.Veco.domain.external.entity.External;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ExternalRepository extends JpaRepository<External, Long> {

    @EntityGraph("workers")
    Optional<External> findByIdWithMembers(Long id);
}
