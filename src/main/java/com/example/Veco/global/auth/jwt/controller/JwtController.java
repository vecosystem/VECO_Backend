package com.example.Veco.global.auth.jwt.controller;

import com.example.Veco.global.apiPayload.ApiResponse;
import com.example.Veco.global.auth.jwt.converter.TokenConverter;
import com.example.Veco.global.auth.jwt.dto.TokenDTO;
import com.example.Veco.global.auth.jwt.dto.TokenPair;
import com.example.Veco.global.auth.jwt.exception.CustomJwtException;
import com.example.Veco.global.auth.jwt.exception.code.JwtErrorCode;
import com.example.Veco.global.auth.jwt.exception.code.JwtSuccessCode;
import com.example.Veco.global.auth.jwt.service.JwtService;
import com.example.Veco.global.auth.jwt.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
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
                ResponseCookie refreshTokenCookie = jwtUtil.createRefreshTokenCookie(newRefreshToken);
                response.addHeader("Set-Cookie", refreshTokenCookie.toString());
            }
            log.info("토큰 재발급 성공: {}", newAccessToken);
            return ApiResponse.onSuccess(TokenConverter.toTokenDTO(newAccessToken));
        } catch (CustomJwtException e) {
            log.error("토큰 재발급 실패: {}", e.getMessage());
            throw new CustomJwtException(JwtErrorCode.JWT_INVALID_TOKEN);
        }
    }

    @PostMapping("/logout")
    @Operation(
            summary = "로그아웃 api",
            description = "리프레쉬 토큰을 쿠키에서 삭제하고, 액세스 토큰을 블랙리스트에 등록합니다."
    )
    public ApiResponse<Void> logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // 토큰 블랙리스트 처리
        String token = request.getHeader("Authorization")
                .replace("Bearer ", "");
        jwtUtil.setBlackList(token);

        // 리프레쉬 토큰 쿠키 삭제
        ResponseCookie refreshTokenCookie = jwtUtil.expireRefreshTokenCookie();
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());

        return ApiResponse.onSuccess(JwtSuccessCode.OK, null);
    }
}
