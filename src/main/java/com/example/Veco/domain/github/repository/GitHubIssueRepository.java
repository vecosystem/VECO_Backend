package com.example.Veco.domain.github.repository;

import com.example.Veco.domain.github.entity.GitHubIssue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GitHubIssueRepository extends JpaRepository<GitHubIssue, Long> {
}
