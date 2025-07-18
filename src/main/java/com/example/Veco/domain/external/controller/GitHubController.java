package com.example.Veco.domain.external.controller;

import com.example.Veco.domain.external.config.GitHubConfig;
import com.example.Veco.domain.external.service.GitHubService;
import com.example.Veco.domain.mapping.GithubInstallation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/github")
@Slf4j
public class GitHubController {

    private final GitHubService gitHubService;
    private final GitHubConfig gitHubConfig;

    @GetMapping("/connect")
    public String connectGithub(@RequestParam("teamId") String teamId){
        String authUrl = String.format(
                "https://github.com/login/oauth/authorize?client_id=%s&redirect_uri=%s&scope=%s&state=%s",
                gitHubConfig.getOauth().getClientId(),
                gitHubConfig.getOauth().getRedirectUri(),
                "read:user,repo,admin:repo_hook",
                teamId
        );

        return "redirect:" + authUrl;
    }

    // TODO : 깃허브 연동 시 어느 팀과 연동되는 건지 저장이 필요

    @GetMapping("/oauth/callback")
    public String githubOAuthCallback(@RequestParam("code") String code,
                                      @RequestParam("state") String state,
                                      Model model) {
        try {

            // GitHub Access Token 획득 및 사용자 정보 저장
//            User user = gitHubService.connectGitHubAccount(code);

            // GitHub App 설치 페이지로 리다이렉트
            String githubAppInstallUrl = String.format(
                    "https://github.com/apps/psb3707/installations/new?state=%s",
                    state
            );

            return "redirect:" + githubAppInstallUrl;

        } catch (Exception e) {
            model.addAttribute("error", "GitHub 연동 중 오류가 발생했습니다: " + e.getMessage());
            return "dashboard/index";
        }
    }

    @GetMapping("/installation/callback")
    public String appInstallationCallback(@RequestParam("state") Long state,
                                          @RequestParam("installation_id") Long installationId) {
        log.info("teamId : {}", state);
        log.info("installationId : {}", installationId);

        gitHubService.saveInstallationInfo(state, installationId);

        return "redirect:/";
    }
}
