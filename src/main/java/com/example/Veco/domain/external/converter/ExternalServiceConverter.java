package com.example.Veco.domain.external.converter;

import com.example.Veco.domain.external.entity.ExternalService;
import com.example.Veco.global.enums.ExtServiceType;

public class ExternalServiceConverter {

    // Data -> ExternalService (Slack)
    public static ExternalService toExternalService(
            ExtServiceType type,
            String token,
            String channelId
    ){
        return ExternalService.builder()
                .serviceType(type)
                .accessToken(token)
                .slackDefaultChannelId(channelId)
                .build();
    }
}
