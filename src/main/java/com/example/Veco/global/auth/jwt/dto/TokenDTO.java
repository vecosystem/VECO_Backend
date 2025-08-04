package com.example.Veco.global.auth.jwt.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
public record TokenDTO(
        String accessToken
) { }
