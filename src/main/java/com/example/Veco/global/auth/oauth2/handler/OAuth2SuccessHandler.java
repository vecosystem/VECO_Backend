package com.example.Veco.global.auth.oauth2.handler;

import com.example.Veco.domain.member.enums.MemberRole;
import com.example.Veco.domain.member.service.MemberCommandService;
import com.example.Veco.global.auth.oauth2.CustomOAuth2User;
import com.example.Veco.global.auth.oauth2.exception.OAuth2Exeception;
import com.example.Veco.global.auth.oauth2.exception.code.OAuth2ErrorCode;
import com.example.Veco.global.auth.user.userdetails.CustomUserDetails;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import com.example.Veco.global.auth.jwt.util.JwtUtil;

import java.io.IOException;
import java.nio.charset.StandardCharsets;


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

        String flow = getFlow(request);

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

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(customUserDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // 최초 로그인
        if (userDetails.getMember().getWorkSpace() == null) {
            Member updatedMember = member.toBuilder()
                    .role(MemberRole.USER)
                    .refreshToken(refreshToken)
                    .build();
            memberCommandService.saveMember(updatedMember);

            log.info("Saved new member with id: {}", updatedMember.getId());

            // 워크스페이스 생성
            if (flow.equals("create")) {
                redirectURL = UriComponentsBuilder.fromUriString("https://veco-eight.vercel.app/onboarding/workspace")
                        .build()
                        .encode(StandardCharsets.UTF_8)
                        .toUriString();
            // 워크스페이스 참여
            } else if (flow.equals("join")) {
                redirectURL = UriComponentsBuilder.fromUriString("https://veco-eight.vercel.app/onboarding/input-pw")
                        .build()
                        .encode(StandardCharsets.UTF_8)
                        .toUriString();
            } else {
                throw new OAuth2Exeception(OAuth2ErrorCode.OAUTH2_INVALID_STATE);
            }
        // 기존 회원
        } else {
            Member updatedMember = member.toBuilder()
                    .refreshToken(refreshToken)
                    .build();
            memberCommandService.saveMember(updatedMember);

            log.info("Updated existing member with id: {}", updatedMember.getId());

            redirectURL = UriComponentsBuilder.fromUriString("https://veco-eight.vercel.app/workspace")
                    .build()
                    .encode(StandardCharsets.UTF_8)
                    .toUriString();
        }

        getRedirectStrategy().sendRedirect(request, response, redirectURL);
    }

    public String getFlow(HttpServletRequest request) {

        HttpSession session = request.getSession();
        String flow = (String) session.getAttribute("flow");
        session.removeAttribute("flow");

        return flow != null ? flow : "create";
    }
}