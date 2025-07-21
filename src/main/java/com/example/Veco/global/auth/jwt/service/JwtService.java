package com.example.Veco.global.auth.jwt.service;

import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.member.repository.MemberRepository;
import com.example.Veco.global.auth.jwt.dto.TokenPair;
import com.example.Veco.global.auth.jwt.exception.code.JwtErrorCode;
import com.example.Veco.global.auth.jwt.util.JwtUtil;
import com.example.Veco.global.auth.user.userdetails.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.Cookie;
import com.example.Veco.global.auth.jwt.exception.CustomJwtException;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {

    private final JwtUtil jwtUtil;
    private final MemberRepository memberRepository; // DB or Redis 저장소

    public String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies()) {
            if ("refreshToken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }

    public TokenPair reissueAccessToken(String refreshToken) throws CustomJwtException {
        if (!jwtUtil.isValid(refreshToken)) {
            log.error("유효하지 않은 RefreshToken: {}", refreshToken);
            throw new CustomJwtException(JwtErrorCode.JWT_INVALID_TOKEN);
        }

        if (jwtUtil.isTokenExpired(refreshToken)) {
            log.error("만료된 RefreshToken: {}", refreshToken);
            throw new CustomJwtException(JwtErrorCode.JWT_EXPIRED_TOKEN);
        }

        Member member = jwtUtil.getMemberByToken(refreshToken);
        CustomUserDetails customUserDetails = new CustomUserDetails(member);

        if (jwtUtil.isExpiringSoon(refreshToken)) {
            // RefreshToken이 3일 이내 만료될 예정인 경우, 새로운 RefreshToken을 생성
            String newRefreshToken = jwtUtil.createRefreshToken(customUserDetails);
            member.setRefreshToken(newRefreshToken);
            memberRepository.save(member);
            refreshToken = newRefreshToken;
            log.info("새로운 RefreshToken 생성: {}", refreshToken);
        } else {
            refreshToken = null;
        }

        return TokenPair.builder()
                .accessToken(jwtUtil.createAccessToken(customUserDetails))
                .refreshToken(refreshToken)
                        .build();
    }
}