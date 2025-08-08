package com.example.Veco.domain.external.service;

import com.example.Veco.domain.external.converter.GitHubConverter;
import com.example.Veco.domain.external.dto.response.GitHubResponseDTO;
import com.example.Veco.domain.external.exception.GitHubException;
import com.example.Veco.domain.external.exception.code.GitHubErrorCode;
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
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

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

    public GitHubResponseDTO.GitHubAppInstallationDTO getInstallationInfo(Long teamId) {

        GithubInstallation githubInstallation = gitHubInstallationRepository.findById(teamId)
                .orElseThrow(() -> new GitHubException(GitHubErrorCode.INSTALLATION_INFO_NOT_FOUND));

        return GitHubConverter.toGitHubAppInstallationDTO(githubInstallation);
    }

    private Long getInstallationId(Long teamId) {
        return gitHubInstallationRepository.findByTeamId(teamId)
                .orElseThrow().getInstallationId();
    }

    public Mono<Long> getInstallationIdAsync(Long teamId) {
        return Mono.fromCallable(() -> getInstallationId(teamId))
                .subscribeOn(Schedulers.boundedElastic()); // DB 조회를 별도 스레드에서 실행
    }

}
