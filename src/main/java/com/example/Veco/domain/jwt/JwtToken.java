package com.example.Veco.domain.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class JwtToken {
    private String granType;
    private String accessToken;
    private String refreshToken;
}
