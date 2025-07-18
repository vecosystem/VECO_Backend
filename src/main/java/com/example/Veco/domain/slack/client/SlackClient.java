package com.example.Veco.domain.slack.client;

import com.example.Veco.domain.slack.dto.SlackDTO;
import com.example.Veco.global.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "SlackClient",
        url = "https://slack.com/api",
        configuration = FeignConfig.class
)
public interface SlackClient {

    // 임시 AccessToken -> AccessToken
    @PostMapping("/oauth.v2.access")
    SlackDTO.Token getToken(
            @RequestParam("code") String code,
            @RequestParam("client_id") String clientId,
            @RequestParam("client_secret") String clientSecret
    );

    // 채널 리스트 확인
    @GetMapping("/conversations.list")
    SlackDTO.ConversationList getConversationList(
            @RequestHeader("Authorization") String token
    );
}
