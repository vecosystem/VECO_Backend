package com.example.Veco.domain.mapping.repository;

import com.example.Veco.domain.mapping.entity.Link;
import com.example.Veco.domain.workspace.entity.WorkSpace;
import com.example.Veco.global.enums.ExtServiceType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LinkRepository extends JpaRepository<Link, Long> {
    Optional<Link> findLinkByWorkspaceAndExternalService_ServiceType(WorkSpace workspace, ExtServiceType externalServiceServiceType);
}
