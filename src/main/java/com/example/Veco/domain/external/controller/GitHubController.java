package com.example.Veco.domain.external.controller;

import com.example.Veco.domain.external.config.GitHubConfig;
import com.example.Veco.domain.external.service.GitHubService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "GitHub 연동 API", description = "GitHub 외부 툴 연동을 위한 인증 및 콜백 API")
public class GitHubController {

    private final GitHubService gitHubService;
    private final GitHubConfig gitHubConfig;

    @Operation(
            summary = "GitHub 연동 시작",
            description = "GitHub OAuth 인증을 시작하여 GitHub으로 리다이렉트합니다. " +
                    "이 API는 브라우저에서 직접 호출해야 하며, GitHub 인증 페이지로 이동합니다.",
            hidden = false
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "302",
                    description = "GitHub OAuth 인증 페이지로 리다이렉트"
            )
    })
    @GetMapping("/connect")
    public String connectGithub(
            @Parameter(description = "연동할 팀 ID", required = true, example = "1") 
            @RequestParam("teamId") Long teamId){
        String authUrl = String.format(
                "https://github.com/login/oauth/authorize?client_id=%s&redirect_uri=%s&scope=%s&state=%d",
                gitHubConfig.getOauth().getClientId(),
                gitHubConfig.getOauth().getRedirectUri(),
                "read:user,repo,admin:repo_hook",
                teamId
        );

        return "redirect:" + authUrl;
    }

    // TODO : 깃허브 연동 시 어느 팀과 연동되는 건지 저장이 필요

    @Operation(
            summary = "GitHub OAuth 콜백",
            description = "GitHub OAuth 인증 완료 후 호출되는 콜백 API입니다. " +
                    "GitHub에서 자동으로 호출하며, 사용자가 직접 호출할 필요는 없습니다.",
            hidden = true
    )
    @GetMapping("/oauth/callback")
    public String githubOAuthCallback(
            @Parameter(description = "GitHub에서 발급한 인증 코드") @RequestParam("code") String code,
            @Parameter(description = "팀 ID (상태 값)") @RequestParam("state") String state,
            Model model) {
        try {

            // GitHub Access Token 획득 및 사용자 정보 저장
//            User user = gitHubService.connectGitHubAccount(code);

            // GitHub App 설치 페이지로 리다이렉트
            String githubAppInstallUrl = String.format(
                    "https://github.com/apps/VecoApp/installations/new?state=%s",
                    state
            );

            return "redirect:" + githubAppInstallUrl;

        } catch (Exception e) {
            model.addAttribute("error", "GitHub 연동 중 오류가 발생했습니다: " + e.getMessage());
            return "dashboard/index";
        }
    }

}
