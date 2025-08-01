package com.example.Veco.domain.external.util;

import com.example.Veco.domain.external.config.GitHubConfig;
import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;

@Component
@RequiredArgsConstructor
@Slf4j
public class GitHubJwtProvider {
    private final GitHubConfig gitHubConfig;
    private PrivateKey privateKey;

    @PostConstruct
    public void init() {
        this.privateKey = loadPrivateKey();
        log.info("GitHub App private key 로딩 완료");
    }

    public String generateJwt() {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis - 60000);
        Date exp = new Date(nowMillis + 600000);

        return Jwts.builder()
                .issuedAt(now)
                .expiration(exp)
                .issuer(gitHubConfig.getApp().getId())
                .signWith(privateKey, Jwts.SIG.RS256)
                .compact();
    }

    private PrivateKey loadPrivateKey() {
        try {
            String privateKeyPath = gitHubConfig.getApp().getPrivateKeyPath();
            String content;

            if (privateKeyPath.startsWith("classpath:")) {
                String resourcePath = privateKeyPath.substring("classpath:".length());
                InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath);
                if (inputStream == null) {
                    throw new FileNotFoundException("Private key 파일을 찾을 수 없습니다: " + resourcePath);
                }
                content = new String(inputStream.readAllBytes());
            } else {
                content = Files.readString(Paths.get(privateKeyPath));
            }

            // ✅ BouncyCastle로 안전하게 파싱
            return parsePrivateKeyWithBouncyCastle(content);

        } catch (Exception e) {
            log.error("GitHub App private key 로딩 실패: {}", e.getMessage());
            throw new RuntimeException("Private key 로딩 실패", e);
        }
    }

    private PrivateKey parsePrivateKeyWithBouncyCastle(String keyContent) throws Exception {
        try (StringReader stringReader = new StringReader(keyContent);
             PEMParser pemParser = new PEMParser(stringReader)) {

            Object pemObject = pemParser.readObject();
            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();

            if (pemObject instanceof PEMKeyPair) {
                // PKCS#1 형식 (RSA PRIVATE KEY)
                PEMKeyPair keyPair = (PEMKeyPair) pemObject;
                log.info("PKCS#1 형식의 private key 감지");
                return converter.getPrivateKey(keyPair.getPrivateKeyInfo());

            } else if (pemObject instanceof PrivateKeyInfo) {
                // PKCS#8 형식 (PRIVATE KEY)
                log.info("PKCS#8 형식의 private key 감지");
                return converter.getPrivateKey((PrivateKeyInfo) pemObject);

            } else {
                throw new IllegalArgumentException("지원하지 않는 private key 형식입니다: " + pemObject.getClass());
            }
        }
    }
}
