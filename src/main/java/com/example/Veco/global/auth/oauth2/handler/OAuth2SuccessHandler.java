package com.example.Veco.global.auth.oauth2.handler;

import com.example.Veco.domain.member.enums.MemberRole;
import com.example.Veco.domain.member.service.MemberCommandService;
import com.example.Veco.global.auth.jwt.converter.TokenConverter;
import com.example.Veco.global.auth.jwt.dto.TokenDTO;
import com.example.Veco.global.auth.oauth2.CustomOAuth2User;
import com.example.Veco.global.auth.user.userdetails.CustomUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import com.example.Veco.global.auth.jwt.util.JwtUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;

import com.example.Veco.domain.member.entity.Member;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

@Slf4j
@RequiredArgsConstructor
@Component
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final MemberCommandService memberCommandService;
    private final JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpServletResponse.SC_OK);

        CustomOAuth2User userDetails = (CustomOAuth2User) authentication.getPrincipal();
        Member member = userDetails.getMember();
        CustomUserDetails customUserDetails = new CustomUserDetails(member);
        String redirectURL;

        String refreshToken = jwtUtil.createRefreshToken(customUserDetails);
        Cookie refreshTokenCookie = jwtUtil.createRefreshTokenCookie(refreshToken);
        response.addCookie(refreshTokenCookie);

        // 최초 로그인
        if (userDetails.getMember().getRole() == null || userDetails.getMember().getRole() == MemberRole.GUEST) {
            member.setName(member.getName());
            member.setRole(MemberRole.USER);

            member.setRefreshToken(refreshToken);
            memberCommandService.saveMember(member);

            redirectURL = UriComponentsBuilder.fromUriString("https://veco-eight.vercel.app/onboarding")
                    .build()
                    .encode(StandardCharsets.UTF_8)
                    .toUriString();
        } else {
            redirectURL = UriComponentsBuilder.fromUriString("https://veco-eight.vercel.app/workspace")
                    .build()
                    .encode(StandardCharsets.UTF_8)
                    .toUriString();
        }

        // 인증용 객체 생성 및 SecurityContext 설정
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(customUserDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        getRedirectStrategy().sendRedirect(request, response, redirectURL);
    }
}