package com.example.Veco.domain.external.repository;

import com.example.Veco.domain.external.entity.ExternalService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExternalServiceRepository extends JpaRepository<ExternalService,Long> {
}
