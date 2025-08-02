package com.example.Veco.global.auth.oauth2.handler;

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
import org.springframework.http.ResponseCookie;
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
        ResponseCookie refreshTokenCookie = jwtUtil.createRefreshTokenCookie(refreshToken);
        response.addHeader("Set-Cookie", refreshTokenCookie.toString());

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(customUserDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authToken);

        member.updateRefreshToken(refreshToken);
        memberCommandService.saveMember(member);
        // 최초 로그인
        // TODO: 테스트용 주석 처리, 이후 해제 필요
//        if (userDetails.getMember().getWorkSpace() == null) {
//            // 워크스페이스 생성
//            if (flow.equals("create")) {
//                redirectURL = UriComponentsBuilder.fromUriString("http://localhost:5173/onboarding/workspace")
//                        .build()
//                        .encode(StandardCharsets.UTF_8)
//                        .toUriString();
//                // 워크스페이스 참여
//            } else if (flow.equals("join")) {
//                redirectURL = UriComponentsBuilder.fromUriString("http://localhost:5173/onboarding/input-pw")
//                        .build()
//                        .encode(StandardCharsets.UTF_8)
//                        .toUriString();
//            } else {
//                throw new OAuth2Exeception(OAuth2ErrorCode.OAUTH2_INVALID_STATE);
//            }
//            // 기존 회원
//        } else {
//            redirectURL = UriComponentsBuilder.fromUriString("http://localhost:5173/workspace")
//                    .build()
//                    .encode(StandardCharsets.UTF_8)
//                    .toUriString();
//        }

        redirectURL = UriComponentsBuilder.fromUriString("http://localhost:5173/onboarding/workspace")
                        .build()
                        .encode(StandardCharsets.UTF_8)
                        .toUriString();

        getRedirectStrategy().sendRedirect(request, response, redirectURL);
    }

    public String getFlow(HttpServletRequest request) {

        HttpSession session = request.getSession();
        String flow = (String) session.getAttribute("flow");
        session.removeAttribute("flow");

        return flow != null ? flow : "create";
    }
}