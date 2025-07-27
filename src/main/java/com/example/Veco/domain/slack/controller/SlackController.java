package com.example.Veco.domain.slack.controller;

import com.example.Veco.domain.slack.dto.SlackResDTO;
import com.example.Veco.domain.slack.service.SlackCommandService;
import com.example.Veco.global.apiPayload.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/slack")
public class SlackController {

    // 서비스
    private final SlackCommandService slackCommandService;

    // Slack Callback: 비즈니스 로직 시작 지점
    @GetMapping("/callback")
    public ApiResponse<SlackResDTO.InstallApp> installApp(
            @RequestParam String code,
            @RequestParam String state
    ){
        return ApiResponse.onSuccess(slackCommandService.installApp(code, state));
    }
}
