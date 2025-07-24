package com.example.Veco.global.auth.jwt.dto;

import lombok.Builder;

@Builder
public record TokenDTO(
        String accessToken
) { }
