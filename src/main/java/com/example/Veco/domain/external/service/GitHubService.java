package com.example.Veco.domain.external.service;

import com.example.Veco.domain.mapping.GithubInstallation;
import com.example.Veco.domain.mapping.repository.GitHubInstallationRepository;
import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.team.entity.Team;
import com.example.Veco.domain.team.exception.TeamException;
import com.example.Veco.domain.team.exception.code.TeamErrorCode;
import com.example.Veco.domain.team.repository.TeamRepository;
import com.example.Veco.global.apiPayload.exception.VecoException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class GitHubService {

    private final GitHubInstallationRepository gitHubInstallationRepository;
    private final TeamRepository teamRepository;

    public void saveInstallationInfo(Long teamId, Long installationId) {

        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new TeamException(TeamErrorCode._NOT_FOUND));


        GithubInstallation info = GithubInstallation.builder()
                .team(team)
                .installationId(installationId)
                .build();

        gitHubInstallationRepository.save(info);
    }


}
