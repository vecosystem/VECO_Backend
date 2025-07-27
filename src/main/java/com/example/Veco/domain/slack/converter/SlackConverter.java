package com.example.Veco.domain.slack.converter;

import com.example.Veco.domain.slack.dto.SlackResDTO;

import java.time.LocalDateTime;

public class SlackConverter {

    // App 설치, 연동 DTO
    public static SlackResDTO.InstallApp toInstallApp(
            Long id,
            LocalDateTime linkedAt
    ){
        return SlackResDTO.InstallApp.builder()
                .workspaceId(id)
                .linkedAt(linkedAt)
                .build();
    }
}
