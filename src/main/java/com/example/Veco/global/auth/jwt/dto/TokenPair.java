package com.example.Veco.global.auth.jwt.dto;

import lombok.Builder;

@Builder
public record TokenPair (
        String accessToken,
        String refreshToken
) { }
