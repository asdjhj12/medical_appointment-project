package com.medical.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenUtil {
    private static final String CLAIM_KEY_USERNAME = "sub";
    private static final String CLAIM_KEY_CREATED = "created";

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(secret);
    }

    private String generateToken(Map<String, Object> claims) {
        return JWT.create()
                .withClaim(CLAIM_KEY_USERNAME, (String) claims.get(CLAIM_KEY_USERNAME))
                .withClaim(CLAIM_KEY_CREATED, (Date) claims.get(CLAIM_KEY_CREATED))
                .withExpiresAt(generateExpirationDate())
                .sign(getAlgorithm());
    }

    private DecodedJWT getDecodedJWT(String token) {
        return JWT.require(getAlgorithm()).build().verify(token);
    }

    public String getUserNameFromToken(String token) {
        try {
            DecodedJWT jwt = getDecodedJWT(token);
            return jwt.getClaim(CLAIM_KEY_USERNAME).asString();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean validateToken(String token) {
        try {
            DecodedJWT jwt = getDecodedJWT(token);
            return jwt.getExpiresAt().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + expiration);
    }

    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USERNAME, username);
        claims.put(CLAIM_KEY_CREATED, new Date());
        return generateToken(claims);
    }

    public boolean canRefresh(String token) {
        try {
            DecodedJWT jwt = getDecodedJWT(token);
            return jwt.getExpiresAt().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }

    public String refreshToken(String token) {
        String username = getUserNameFromToken(token);
        return generateToken(username);
    }
}
