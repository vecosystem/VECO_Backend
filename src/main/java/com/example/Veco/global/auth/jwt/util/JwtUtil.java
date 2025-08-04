package com.example.Veco.global.auth.jwt.util;

import com.example.Veco.domain.member.entity.Member;
import com.example.Veco.domain.member.repository.MemberRepository;
import com.example.Veco.global.auth.jwt.exception.CustomJwtException;
import com.example.Veco.global.auth.jwt.exception.code.JwtErrorCode;
import com.example.Veco.global.auth.user.userdetails.CustomUserDetails;
import com.example.Veco.global.redis.util.RedisUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtUtil {

    private final SecretKey secretKey; // Key
    private final Duration accessExpiration;
    private final Duration refreshExpiration;
    private final MemberRepository memberRepository;
    private final RedisUtil redisUtil;

    // 토큰 접두사: Redis 전용
    private final String BLACK_LIST_PREFIX = "token_blacklist:";

    public JwtUtil(
            @Value("${spring.jwt.secret}") String secretKey,
            @Value("${spring.jwt.token.access-expiration}") long accessExpiration,
            @Value("${spring.jwt.token.refresh-expiration}") long refreshExpiration, MemberRepository memberRepository,
            RedisUtil redisUtil
    ) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        this.accessExpiration = Duration.ofMillis(accessExpiration);
        this.refreshExpiration = Duration.ofMillis(refreshExpiration);
        this.memberRepository = memberRepository;
        this.redisUtil = redisUtil;
    }

    // AccessToken 생성
    public String createAccessToken(CustomUserDetails user) {
        return createToken(user, accessExpiration);
    }

    // RefreshToken 생성
    public String createRefreshToken(CustomUserDetails user) {
        return createToken(user, refreshExpiration);
    }

    /** Token 유효기간 가져오기
     *
     * @param token 유효기간 가져올 토큰
     * @return 토큰의 유효기간을 가져옵니다
     */
    public Date getTokenExpiration(String token) {
        try {
            return getClaims(token).getPayload().getExpiration(); // Parsing해서 유효기간 가져오기
        } catch (CustomJwtException e) {
            return null;
        }
    }

    /** 토큰에서 이메일 가져오기
     *
     * @param token 유저 정보를 추출할 토큰
     * @return 유저 이메일을 토큰에서 추출합니다
     */
    public String getUsername(String token) {
        try {
            return getClaims(token).getPayload().getSubject(); // Parsing해서 Subject 가져오기
        } catch (CustomJwtException e) {
            return null;
        }
    }

    /** 토큰 유효성 확인
     *
     * @param token 유효한지 확인할 토큰
     * @return True, False 반환합니다
     */
    public boolean isRefreshTokenValid(String token) {
        try {
            getClaims(token);
            return true;
        } catch (CustomJwtException e) {
            return false;
        }
    }

    public boolean isAccessTokenValid(String token) {
        try {
            // 토큰이 블랙리스트에 존재하는지 확인
            if (isBlackList(token)){
                return false;
            }
            getClaims(token);
            return true;
        } catch (CustomJwtException e) {
            return false;
        }
    }

    /** 토큰 블랙리스트 설정 (Redis)
     * @param token 블랙리스트에 넣을 토큰 -> token_blacklist : token
     */
    public void setBlackList(String token) {
        // 토큰 유효기간 확인
        Date exp = getTokenExpiration(token);
        // 블랙리스트 유효기간 설정: 현재 시간 - 만료기간 + 1분
        Duration duration = Duration.between(Instant.now(), exp.toInstant())
                .plus(Duration.ofMinutes(1));
        redisUtil.save(BLACK_LIST_PREFIX+token, "", duration);
    }

    /** 토큰 블랙리스트에 존재하는지 확인 (Redis)
     *
     * @param token 존재 여부 확인할 토큰
     * @return True, False를 반환합니다
     */
    public boolean isBlackList(String token) {
        return redisUtil.hasKey(BLACK_LIST_PREFIX+token);
    }

    // 토큰 생성
    private String createToken(CustomUserDetails user, Duration expiration) {
        Instant now = Instant.now();

        // 인가 정보
        String authorities = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .subject(user.getUsername()) // 소셜 uid를 username으로 사용
                .claim("role", authorities)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expiration)))
                .signWith(secretKey) // sign할 Key
                .compact();
    }

    // 토큰 정보 가져오기
    private Jws<Claims> getClaims(String token) throws CustomJwtException {
        Jws<Claims> claims = Jwts.parser()
                .verifyWith(secretKey)
                .clockSkewSeconds(60)
                .build()
                .parseSignedClaims(token);

        if (claims.getPayload().getExpiration().before(new Date())) {
            throw new CustomJwtException(JwtErrorCode.JWT_EXPIRED_TOKEN);
        }

        return claims;
    }

    public Member getMemberByToken(String token) {
        try {
            Jws<Claims> claims = getClaims(token);
            String uid = claims.getPayload().getSubject(); // Subject에서 소셜 uid 추출
            Member member = memberRepository.findBySocialUid(uid)
                    .orElseThrow(
                        () -> {
                            log.error("토큰에서 추출한 소셜 uid로 멤버를 찾을 수 없습니다: {}", uid);
                            return new CustomJwtException(JwtErrorCode.JWT_INVALID_TOKEN);
                        } // TODO 예외 처리 개선 필요
                    );
            if (!member.getRefreshToken().equals(token)) {
                log.error("토큰과 멤버의 RefreshToken이 일치하지 않습니다: {} != {}", member.getRefreshToken(), token);
                throw new CustomJwtException(JwtErrorCode.JWT_INVALID_TOKEN); // 토큰이 일치하지 않는 경우 예외 처리
            }
            return member;
        } catch (CustomJwtException e) {
            log.error("토큰에서 멤버 정보를 추출하는 중 오류 발생: {}", e.getMessage());
            throw new CustomJwtException(JwtErrorCode.JWT_INVALID_TOKEN); // 유효하지 않은 토큰인 경우 null 반환
        }
    }

    public Boolean isTokenExpired(String token) {
        try {
            Jws<Claims> claims = getClaims(token);
            log.info("isTokenExpired: {}", getTokenExpiration(token));
            Date expiration = claims.getPayload().getExpiration();
            log.info("expired?: {}", expiration.before(new Date()));
            return expiration.before(new Date());
        } catch (CustomJwtException e) {
            return true; // 토큰이 유효하지 않거나 만료된 경우
        }
    }

    public Boolean isExpiringSoon(String token) {
        try {
            log.info("토큰 만료일: {}", getTokenExpiration(token));
            Jws<Claims> claims = getClaims(token);
            Date expiration = claims.getPayload().getExpiration();
            Instant now = Instant.now();
            return expiration.toInstant().isBefore(now.plus(Duration.ofDays(3)));
        } catch (CustomJwtException e) {
            log.error("토큰 유효성 검사 중 오류 발생: {}", e.getMessage());
            return false; // 토큰이 유효하지 않은 경우 false 반환
        }
    }

    public ResponseCookie createRefreshTokenCookie(String refreshToken) {
        // refresh 토큰 유효기간 설정
        Date TokenExpiration = getTokenExpiration(refreshToken);
        long exp = TokenExpiration.getTime() - Instant.now().toEpochMilli();
        int maxAge = exp > 0 ? Math.toIntExact(exp / 1000) : 0;

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(maxAge)
                .build();

        return refreshTokenCookie;
    }

    public ResponseCookie expireRefreshTokenCookie() {
        // 만료된 refresh 토큰 쿠키 생성
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(true)
                .sameSite("None")
                .path("/")
                .maxAge(0)
                .build();

        return refreshTokenCookie;
    }
}