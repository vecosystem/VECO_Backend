package com.example.Veco.domain.external.controller;

import com.example.Veco.domain.external.config.GitHubConfig;
import com.example.Veco.domain.external.service.GitHubService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/github")
public class GitHubController {

    private final GitHubService gitHubService;
    private final GitHubConfig gitHubConfig;

    @GetMapping("/connect")
    private String connectGithub(){
        String authUrl = String.format(
                "https://github.com/login/oauth/authorize?client_id=%s&redirect_uri=%s&scope=%s",
                gitHubConfig.getOauth().getClientId(),
                gitHubConfig.getOauth().getRedirectUri(),
                "read:user,repo,admin:repo_hook"
        );

        return "redirect:" + authUrl;
    }

    @GetMapping("/oauth/callback")
    public String githubOAuthCallback(@RequestParam("code") String code,
                                      Model model) {
        try {

            // GitHub Access Token 획득 및 사용자 정보 저장
//            User user = gitHubService.connectGitHubAccount(code);

            // GitHub App 설치 페이지로 리다이렉트
            String githubAppInstallUrl = String.format(
                    "https://github.com/apps/psb3707/installations/new"
            );

            return "redirect:" + githubAppInstallUrl;

        } catch (Exception e) {
            model.addAttribute("error", "GitHub 연동 중 오류가 발생했습니다: " + e.getMessage());
            return "dashboard/index";
        }
    }
}
