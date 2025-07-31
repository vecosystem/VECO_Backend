package com.example.Veco.domain.external.entity;

import com.example.Veco.global.enums.ExtServiceType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "external_service")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ExternalService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "service_type")
    @Enumerated(EnumType.STRING)
    private ExtServiceType serviceType;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "slack_default_channel_id")
    private String slackDefaultChannelId;

    // update
    public void updateAccessToken(String accessToken){
        this.accessToken = accessToken;
    }
    public void updateSlackDefaultChannelId(String slackDefaultChannelId){
        this.slackDefaultChannelId = slackDefaultChannelId;
    }
}
