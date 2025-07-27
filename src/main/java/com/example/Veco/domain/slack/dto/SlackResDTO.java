package com.example.Veco.domain.slack.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public class SlackResDTO {


    // 비즈니스 응답 DTO
    // 설치, 연동 완료
    @Builder
    public record InstallApp(
            Long workspaceId,
            LocalDateTime linkedAt
    ){}

    // Slack 응답 DTO
    // Bot Access, Refresh Token 발급
    @Builder
    public record ExchangeAccessToken(
            boolean ok,
            String access_token,
            String token_type,
            String scope,
            String bot_user_id,
            String app_id,
            Team team,
            Enterprise enterprise,
            AuthedUser authed_user,
            String error // 에러 발생시 상태 메시지
    ){}

    // 채널 리스트 조회
    @Builder
    public record GetChannelList(
            boolean ok,
            List<Channel> channels,
            String error // 에러 발생시 상태 메시지
    ){}

    // 부가 요소
    // Team
    private record Team(
            String name,
            String id
    ){}

    // Enterprise
    private record Enterprise(
            String name,
            String id
    ){}

    // AuthedUser
    private record AuthedUser(
            String id,
            String scope,
            String access_token,
            String token_type
    ){}

    // Channel: 필요한 값만 받아오기
    public record Channel(
            String id,
            boolean is_general
    ){}
}
