package com.example.Veco.domain.slack.controller;

import com.example.Veco.domain.slack.dto.SlackResDTO;
import com.example.Veco.domain.slack.exception.code.SlackSuccessCode;
import com.example.Veco.domain.slack.service.SlackCommandService;
import com.example.Veco.global.apiPayload.ApiResponse;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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
    public RedirectView slackConnect(
            @RequestHeader("Authorization") @Parameter(hidden = true)
            String token
    ){
        return slackCommandService.redirectSlackOAuth(token);
    }

    // Slack Callback: 비즈니스 로직 시작 지점
    @Hidden
    @GetMapping("/callback")
    public ApiResponse<SlackResDTO.InstallApp> installApp(
            @RequestParam String code,
            @RequestParam String state
    ){
        return ApiResponse.onSuccess(SlackSuccessCode.CONNECTING, slackCommandService.installApp(code, state));
    }
}
