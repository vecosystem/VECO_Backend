package com.example.Veco.domain.external.repository;

import com.example.Veco.domain.external.entity.External;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExternalRepository extends JpaRepository<External, Long> {
}
