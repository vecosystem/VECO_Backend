package com.example.Veco.domain.slack.controller;

import com.example.Veco.domain.slack.dto.SlackResDTO;
import com.example.Veco.domain.slack.exception.code.SlackSuccessCode;
import com.example.Veco.domain.slack.service.SlackCommandService;
import com.example.Veco.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequiredArgsConstructor
@RequestMapping("/slack")
public class SlackController {

    // 서비스
    private final SlackCommandService slackCommandService;

    // Slack 연동: 연동 시작 지점
    @GetMapping("/connect")
    public RedirectView slackConnect(
//            @RequestHeader("Authorization") @Parameter(hidden = true)
//            String token
    ){
        String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiIxMTMyMjkzNTA3ODM4Nzc1Mzc0NjIiLCJyb2xlIjoiIiwiaWF0IjoxNzUzNzMwODA0LCJleHAiOjE3NTM3MzQ0MDR9.ji6y-1TT_ctWTXMKf5uFup0n7pnTB_7lPs3sPapTMypvYMOuK9Bt-BcnUJKCPIAiZ-otRGLa5QgCSxs9-pa6Ig";
        return slackCommandService.redirectSlackOAuth(token);
    }

    // Slack Callback: 비즈니스 로직 시작 지점
    @GetMapping("/callback")
    public ApiResponse<SlackResDTO.InstallApp> installApp(
            @RequestParam String code,
            @RequestParam String state
    ){
        return ApiResponse.onSuccess(SlackSuccessCode.CONNECTING, slackCommandService.installApp(code, state));
    }
}
