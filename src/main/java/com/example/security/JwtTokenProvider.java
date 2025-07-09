package com.example.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    @Value("(jwt.secret")
    private String secret;

    private Key key;

    private final long tokenValidTime = 1000L * 60 * 60;

    @PostConstruct
    protected void init(){
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    // 1. 토큰 생성
    public String createToken(String username, String role){
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("role", role);

        Date now = new Date();
        Date expiry = new Date(now.getTime() + tokenValidTime);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiry)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    // 2. 인증 객체 반환
    public Authentication getAuthentication(String token){
        String username = getUsernameFromToken(token);
        String role = getRoleFromToken(token);
        return new UsernamePasswordAuthenticationToken(
                username,
                "",
                List.of(new SimpleGrantedAuthority(role))
        );

    }
    // 3. 토큰에서 username 추출
    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    // 4. 토큰에서 role 추출
    public String getRoleFromToken(String token) {
        return parseClaims(token).get("role", String.class);
    }

    // 5. 토큰 유효성 검사
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    // 6. Claims 파싱
    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
