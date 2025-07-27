package com.example.Veco.domain.external.repository;

import com.example.Veco.domain.external.entity.GitHubIssue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GitHubIssueRepository extends JpaRepository<GitHubIssue, Long> {
}
