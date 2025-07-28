package com.example.Veco.domain.slack.util;

import com.example.Veco.domain.slack.dto.SlackResDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class SlackUtil {

    // 비공개 값
    @Value("${slack.client-id}")
    private String clientId;
    @Value("${slack.client-secret}")
    private String clientSecret;

    // WebClient 생성
    WebClient client = WebClient.builder()
            .baseUrl("https://slack.com/api")
            .defaultHeader("Content-Type", "application/x-www-form-urlencoded")
            .build();

    // Bot Access Token 발급
    public SlackResDTO.ExchangeAccessToken ExchangeAccessToken(
            String code
    ){
        return client.post()
                .uri("/oauth.v2.access")
                .bodyValue(
                        "code=" + code +
                                "&client_id=" + clientId +
                                "&client_secret=" + clientSecret
                )
                .retrieve()
                .bodyToMono(SlackResDTO.ExchangeAccessToken.class)
                .block();
    }

    // 채널 리스트 조회
    public SlackResDTO.GetChannelList GetChannelList(
            String accessToken
    ){
        return client.get()
                .uri("/conversations.list")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(SlackResDTO.GetChannelList.class)
                .block();
    }

    // 채널 참여
    public SlackResDTO.JoinChannel joinChannel(
            String accessToken,
            String channelId
    ){
        return client.post()
                .uri("/conversations.join")
                .header("Authorization", "Bearer " + accessToken)
                .bodyValue(
                        "channel=" + channelId
                )
                .retrieve()
                .bodyToMono(SlackResDTO.JoinChannel.class)
                .block();
    }

    // 메시지 전송
    public SlackResDTO.PostSlackMessage PostSlackMessage(
            String accessToken, String slackDefaultChannelId
    ){
        return client.post()
                .uri("/chat.postMessage")
                .header("Authorization", "Bearer " + accessToken)
                .bodyValue(
                        "channel=" + slackDefaultChannelId+
                        "&text=" + "test"
                )
                .retrieve()
                .bodyToMono(SlackResDTO.PostSlackMessage.class)
                .block();
    }
}
