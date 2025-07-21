package com.example.Veco.global.auth.jwt.converter;

import com.example.Veco.global.auth.jwt.dto.TokenDTO;

public class TokenConverter {
    public static TokenDTO toTokenDTO(String accessToken) {
        return TokenDTO.builder()
                .accessToken(accessToken)
                .build();
    }
}
