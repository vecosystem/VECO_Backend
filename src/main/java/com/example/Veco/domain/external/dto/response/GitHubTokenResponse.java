package com.example.Veco.domain.external.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitHubTokenResponse {
    private String token;

    @JsonProperty("expires_at")
    private String expiresAt;

    private Map<String, String> permissions;

    @JsonProperty("repository_selection")
    private String repositorySelection;
}
