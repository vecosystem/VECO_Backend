package com.example.Veco.global.auth.jwt.filter;

import com.example.Veco.global.auth.jwt.exception.CustomJwtException;
import com.example.Veco.global.auth.jwt.exception.code.JwtErrorCode;
import com.example.Veco.global.auth.jwt.util.JwtUtil;
import com.example.Veco.global.auth.user.exception.UserException;
import com.example.Veco.global.auth.user.exception.code.UserErrorCode;
import com.example.Veco.global.auth.user.service.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService customUserDetailsService;

    private static final AntPathMatcher pathMatcher = new AntPathMatcher();
    private static final String[] excludePaths = {"/", "/api/test/login/", "/api/token/reissue", "/login-test.html", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**", "/css/**", "/images/**", "/js/**", "/h2-console/**", "/profile"};

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return Arrays.stream(excludePaths)
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            // 토큰 가져오기
            String token = request.getHeader("Authorization");
            // token이 없거나 Bearer가 아니면 넘기기
            if (token == null || !token.startsWith("Bearer ")) {
                filterChain.doFilter(request, response);
                log.error("JWT 토큰이 없거나 Bearer가 아닙니다.");
                return;
            }
            // Bearer이면 추출
            token = token.replace("Bearer ", "");
            // AccessToken 검증하기: 올바른 토큰이면
            if (jwtUtil.isValid(token)) {
                // 토큰에서 이메일 추출
                String uid = jwtUtil.getUsername(token);
                UserDetails user = customUserDetailsService.loadUserByUsername(uid);
                Authentication auth = new UsernamePasswordAuthenticationToken(
                        user,
                        null,
                        user.getAuthorities()
                );
                // 인증 완료 후 SecurityContextHolder에 넣기
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            filterChain.doFilter(request, response);
        } catch (ExpiredJwtException e) {
            throw new CustomJwtException(JwtErrorCode.JWT_EXPIRED_TOKEN);
        } catch (MalformedJwtException e) {
            throw new CustomJwtException(JwtErrorCode.JWT_MALFORMED_TOKEN);
        } catch (UserException e) {
            throw new UserException(UserErrorCode.USER_NOT_FOUND);
        }
    }
}
