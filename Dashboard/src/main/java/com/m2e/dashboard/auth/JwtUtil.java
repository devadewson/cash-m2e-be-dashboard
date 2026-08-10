package com.m2e.dashboard.auth;

import com.m2e.dashboard.config.UserAndCorp;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
public class JwtUtil {

    @Value("${cfo.app.jwtSecret}")
    private String SECRET;

    private static final String BEARER = "Bearer ";

    public String extractUserId(String token) {

        token = extractToken(token);

        String name = Jwts.parserBuilder()
                .setSigningKey(generateSecret())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        return name;
    }

    public UserAndCorp extractUserIdAndCorpId(String token) {

        token = extractToken(token);

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(generateSecret())
                .build()
                .parseClaimsJws(token)
                .getBody();

        UserAndCorp userCorpInfo = new UserAndCorp(claims.getSubject(), String.valueOf(claims.get("corpId")), String.valueOf(claims.get("mainCorpId")));
        log.info("Detected activity from {}", userCorpInfo);
        return userCorpInfo;
    }

    public SecretKey generateSecret() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    private String extractToken(String header) {
        return header.substring(BEARER.length());
    }
}
