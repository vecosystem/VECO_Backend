package com.example.Veco.domain.slack.controller;

import com.example.Veco.domain.slack.service.SlackCommandService;
import com.example.Veco.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequiredArgsConstructor
@RequestMapping("/slack")
@Tag(name = "Slack 연동 API")
public class SlackController {

    // 서비스
    private final SlackCommandService slackCommandService;

    // Slack 연동: 연동 시작 지점
    @Operation(
            summary = "Slack 연동 API By 김주헌",
            description = "워크스페이스와 Slack을 연동합니다." +
                    " 헤더에 JWT Access Token을 실어 보내시면 됩니다." +
                    "요청이 오면 Slack OAuth 화면으로 리다이렉트되는 방식입니다."
    )
    @GetMapping("/connect")
    public ApiResponse<String> slackConnect(
            @RequestHeader("Authorization") @Parameter(hidden = true)
            String token,
            @AuthenticationPrincipal UserDetails user
    ){
        return ApiResponse.onSuccess(slackCommandService.redirectSlackOAuth(token, user));
    }

    // Slack Callback: 비즈니스 로직 시작 지점
    @Hidden
    @GetMapping("/callback")
    public RedirectView installApp(
            @RequestParam String code,
            @RequestParam String state
    ){
        Long teamId = slackCommandService.installApp(code, state);
        String URL = "http://localhost:5173/slack/complete?teamId="+teamId;
        return new RedirectView(URL);
    }
}
