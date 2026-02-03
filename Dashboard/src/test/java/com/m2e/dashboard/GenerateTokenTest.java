package com.m2e.dashboard;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class GenerateTokenTest {

    @Test
    public void generateTokenTest() {
        //String token = generateToken("CONVDRHT", "LASENZA");

        //System.out.println("JWT token:" + token);

        String token = generateToken("ACCSUNI01", "MY000396299");

        System.out.println(token);
    }

    public String generateToken(String userId, String corpId) {
        List<String> roles = Arrays.asList("USER");
        return Jwts.builder()
                .claim("corpId", corpId)
                .claim("mainCorpId", corpId)
                .claim("src", true)
                .claim("roles", roles)
                .setSubject(userId)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + 3600000))
                .signWith(generateSecret())
                .compact();
    }

    public SecretKey generateSecret() {
        String secret = "test-secret-key-1111-2222-3333-4444";
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
}
