package com.talha.academix.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtService {
    
    @Value("${app.jwt.secret}")
    private String secretKey;

    @Value("${app.jwt.expiration}")
    private long jwtExpiration;
    
    public String generateToken(String name, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        System.out.println("DEBUG: name in jwtservice generate token:  " + name);
        return createToken(name, claims);
    }

    private String createToken(String name, Map<String, Object> claims) {
        String token = Jwts.builder()
            .claims(claims)
            .subject(name)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
            .header().add("typ", "JWT").and()
            .signWith(getSignInKey())
            .compact();

            System.out.println("DEBUG: Generated token in jwtservice create method: " + token);
            return token;
            
    }
        public String extractRole(String token) {
            final Claims claims = extractAllClaims(token);
            return claims.get("role", String.class);
        }
    
    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token) {
        return extractName(token, Claims::getSubject);

    }

    public Date extractExpiration(String token) {
        return extractName(token, Claims::getExpiration);
    }

    public Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }



    public <T> T extractName(String token, Function<Claims, T> claimsResolver) {

        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);

    }

private Claims extractAllClaims(String token) {
    return Jwts.parser()
            .verifyWith(getSignInKey())   // NOT setSigningKey
            .build()
            .parseSignedClaims(token)
            .getPayload();
}

}
