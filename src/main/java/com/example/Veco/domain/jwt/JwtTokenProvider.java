package com.example.Veco.domain.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {

    private static final String GRANT_TYPE = "Bearer";
    private static final String AUTHORITIES_KEY = "auth";

    private final SecretKey key;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access-expiration-minutes}") long accessExpMin,
            @Value("${jwt.refresh-expiration-minutes}") long refreshExpMin
    ) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.accessTokenExpiration = accessExpMin * 60 * 1000;
        this.refreshTokenExpiration = refreshExpMin * 60 * 1000;
    }

    public JwtToken generateToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = System.currentTimeMillis();

        String accessToken = createAccessToken(authentication.getName(), authorities, now);
        String refreshToken = createRefreshToken(now);

        return JwtToken.builder()
                .granType(GRANT_TYPE)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    private String createAccessToken(String username, String authorities, long now) {
        Date expiry = new Date(now + accessTokenExpiration);
        return Jwts.builder()
                .subject(username)
                .claim(AUTHORITIES_KEY, authorities)
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    private String createRefreshToken(long now) {
        Date expiry = new Date(now + refreshTokenExpiration);
        return Jwts.builder()
                .expiration(expiry)
                .signWith(key)
                .compact();
    }

    public Authentication getAuthentication(String accessToken) {
        Claims claims = parseClaims(accessToken);

        String authorities = claims.get(AUTHORITIES_KEY, String.class);
        if (authorities == null || authorities.isEmpty()) {
            throw new AuthenticationCredentialsNotFoundException("권한 정보가 없는 토큰입니다.");
        }

        Collection<? extends GrantedAuthority> grantedAuthorities =
                Arrays.stream(authorities.split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        UserDetails principal = new User(claims.getSubject(), "", grantedAuthorities);
        return new UsernamePasswordAuthenticationToken(principal, "", grantedAuthorities);
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰입니다.");
            throw new RuntimeException("Expired JWT Token.", e);
        } catch (SecurityException | MalformedJwtException e) {
            log.warn("잘못된 JWT 서명입니다.");
            throw new RuntimeException("Invalid JWT Token", e);
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 JWT 토큰입니다.");
            throw new RuntimeException("Unsupported JWT Token", e);
        } catch (IllegalArgumentException e) {
            log.warn("JWT 클레임이 비어 있습니다.");
            throw new RuntimeException("JWT claims string is empty.", e);
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.warn("JWT 검증 실패: {}", e.getMessage());
            return false;
        }
    }
}