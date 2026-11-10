package com.eex.domain.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public String generateToken(Integer userId, Integer clientId) {
        try {
            return Jwts.builder()
                    .claim("userId", userId)
                    .claim("clientId", clientId)
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                    .signWith(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                    .compact();
        } catch (Exception e) {
            logger.error("Error generating JWT: " + e.getMessage());
            throw new RuntimeException("Error generating JWT", e);
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            logger.error("Invalid JWT: " + e.getMessage());
            return false;
        }
    }

    public Integer getUserIdFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("userId", Integer.class);
    }

    public Integer getClientIdFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody()
                .get("clientId", Integer.class);
    }

    public Integer getAuthenticatedClientId() {
        // Simule l'extraction du clientId depuis le JWT authentifié
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                .build()
                .parseClaimsJws(getCurrentToken())
                .getBody()
                .get("clientId", Integer.class);
    }

    private String getCurrentToken() {
        // Implémentation fictive pour récupérer le token actuel
        // À remplacer par une méthode réelle pour extraire le token du contexte de sécurité
        return "currentToken";
    }

    public Integer validateAndGetUserId(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(Keys.hmacShaKeyFor(jwtSecret.getBytes()))
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .get("userId", Integer.class);
        } catch (Exception e) {
            logger.error("Invalid or expired token: " + e.getMessage());
            return null;
        }
    }
}