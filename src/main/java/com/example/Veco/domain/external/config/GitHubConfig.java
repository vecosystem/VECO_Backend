package com.example.Veco.domain.external.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Getter
@ConfigurationProperties(prefix = "github")
public class GitHubConfig {
    private Oauth oauth = new Oauth();
    private App app = new App();
    private Api api = new Api();

    @Getter @Setter
    public static class Oauth {
        private String clientId;
        private String clientSecret;
        private String redirectUri;
    }

    @Getter @Setter
    public static class App {
        private String id;
        private String privateKeyPath;
    }

    @Getter @Setter
    public static class Api {
        private String baseUrl;
        private String acceptHeader;
    }

    @PostConstruct
    public void init() {
        System.out.println("GITHUB CONFIGURATION" + oauth.getClientId());
    }
}
