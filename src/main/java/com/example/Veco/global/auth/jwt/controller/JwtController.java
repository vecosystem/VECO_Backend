package com.example.Veco.global.auth.jwt.controller;

import com.example.Veco.global.apiPayload.ApiResponse;
import com.example.Veco.global.auth.jwt.converter.TokenConverter;
import com.example.Veco.global.auth.jwt.dto.TokenDTO;
import com.example.Veco.global.auth.jwt.dto.TokenPair;
import com.example.Veco.global.auth.jwt.exception.CustomJwtException;
import com.example.Veco.global.auth.jwt.exception.code.JwtErrorCode;
import com.example.Veco.global.auth.jwt.service.JwtService;
import com.example.Veco.global.auth.jwt.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/token")
@Slf4j
@Tag(name = "토큰 api")
public class JwtController {
    private final JwtUtil jwtUtil;
    private final JwtService jwtService;

    // 토큰 재발급
    @PostMapping("/reissue")
    @Operation(
            summary = "액세스 토큰 재발급 api",
            description = "refreshToken 쿠키를 이용해 액세스 토큰을 재발급합니다." +
                    "refreshToken의 만료 기간이 3일 이하로 남았을 경우 refreshToken도 함께 재발급됩니다."
    )
    public ApiResponse<TokenDTO> reissue(HttpServletRequest request, HttpServletResponse response) {
        String refreshToken = jwtService.extractRefreshTokenFromCookie(request);

        if (refreshToken == null) {
            throw new CustomJwtException(JwtErrorCode.REFRESH_TOKEN_NOT_FOUND);
        }

        try {
            TokenPair tokenPair = jwtService.reissueAccessToken(refreshToken);
            String newAccessToken = tokenPair.accessToken();
            String newRefreshToken = tokenPair.refreshToken();
            if (newRefreshToken != null) {
                Cookie refreshTokenCookie = jwtUtil.createRefreshTokenCookie(newRefreshToken);
                response.addCookie(refreshTokenCookie);
            }
            return ApiResponse.onSuccess(TokenConverter.toTokenDTO(newAccessToken));
        } catch (CustomJwtException e) {
            log.error("토큰 재발급 실패: {}", e.getMessage());
            throw new CustomJwtException(JwtErrorCode.JWT_INVALID_TOKEN);
        }
    }
}
