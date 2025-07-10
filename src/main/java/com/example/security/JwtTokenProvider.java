package com.example.security;

import com.example.config.JwtProperties;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;


// JWT를 생성하고 검증하는 핵심 유틸 클래스
// 주요 역할 : 1. JWT 토큰 생성, 2. 토큰에서 사용자 정보 추출, 3. 토큰 유효성 검증
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private Key key;

    @PostConstruct
    protected void init() {
        System.out.println("JWT SECRET 길이: " + jwtProperties.getSecret().length());
        this.key = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes());
    }
    // 1. 토큰 생성
    public String createToken(String username, String role) {
        // 주제(subject)로 사용자 이름 설정
        Claims claims = Jwts.claims().setSubject(username);
        // Claims는 JWT 내부에 들어가는 데이터(payload)를 의미함
        // JWT는 3개의 파트로 구성됨 [Header], [Payload], [Signature]
        // PayLoad 안에 들어가는 값이 바로 Claims이다.
        // (즉, 토큰 안에 어떤 사용자  **** 정보나 역할, 만료시간 ****을 이 claims에 담는다.
        claims.put("role", role);

        Date now = new Date();
        Date expiry = new Date(now.getTime() + 1000L * 60 * 60); // 1시간

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiry)
                // 위에 언급된 만료시간을 claims에 직접 넣기보다는 빌더 메서드로 설정하고 있다 !
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
    // 2. JWT에서 사용자 이름(username)을 추출
    // 보안 상 비밀번호는 절대 토큰에 담지 않는다 !!
    // ID (사용자 이름)만 추출 가능하고, 비밀번호는 토큰에 없음
    public String getUsernameFromToken(String token) {
        return parseClaims(token).getSubject();
    }


    // 3. 토큰의 유효성 검증

    public boolean validateToken(String token) {
        try {
            parseClaims(token); // 아래의 3가지 검사가 자동 수행됨
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        String username = getUsernameFromToken(token);
        String role = getRoleFromToken(token);
        return new UsernamePasswordAuthenticationToken(
                username,
                "",
                List.of(new SimpleGrantedAuthority(role))
        );
    }

    public String getRoleFromToken(String token) {
        return parseClaims(token).get("role", String.class);
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                // 이 부분에서 모든 검증이 자동 수행됨 ( 서명, 만료 , 형식)
                .getBody();
    }
}
