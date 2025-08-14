package com.example.Veco.global.auth.oauth2.handler;

import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.member.service.MemberCommandService;
import com.example.Veco.global.auth.jwt.util.JwtUtil;
import com.example.Veco.global.auth.oauth2.CustomOAuth2User;
import com.example.Veco.global.auth.user.userdetails.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final MemberCommandService memberCommandService;
    private final JwtUtil jwtUtil;

    @Value("${cors.allowed-origins}")
    private List<String> allowedOrigins;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        // CORS 헤더 수동 추가
        String origin = request.getHeader("Origin");
        if (origin != null && allowedOrigins.contains(origin)) {
            response.setHeader("Access-Control-Allow-Origin", origin);
            response.setHeader("Access-Control-Allow-Credentials", "true");
        }

        // 유저 정보 가져오기 & 인증 객체 생성
        CustomOAuth2User userDetails = (CustomOAuth2User) authentication.getPrincipal();
        Member member = userDetails.getMember();
        CustomUserDetails customUserDetails = new CustomUserDetails(member);
        String redirectURL;

        // Refresh Token 발급 & 쿠키 설정
        String refreshToken = jwtUtil.createRefreshToken(customUserDetails);
        ResponseCookie refreshTokenCookie = jwtUtil.createRefreshTokenCookie(refreshToken);
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());

        // 인증 객체 저장
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(customUserDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // DB에 Refresh Token 업데이트
        member.updateRefreshToken(refreshToken);
        memberCommandService.saveMember(member);

        // 로딩 화면으로 리다이렉트
        redirectURL = UriComponentsBuilder.fromUriString("https://web.vecoservice.shop/onboarding/loading")
                        .build()
                        .encode(StandardCharsets.UTF_8)
                        .toUriString();

        getRedirectStrategy().sendRedirect(request, response, redirectURL);
    }
}