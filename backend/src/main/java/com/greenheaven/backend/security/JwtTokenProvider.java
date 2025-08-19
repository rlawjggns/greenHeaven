package com.greenheaven.backend.security;

import com.greenheaven.backend.domain.Member;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JwtTokenProvider {

    private final Key key;
    private final Key refreshKey;
    private final Long tokenValidityInSeconds;
    private final Long refreshTokenValidityInSeconds;
    private final String issuer;

    public JwtTokenProvider(
            @Value("${jwt.secret-key}") String secretKey,
            @Value("${jwt.refresh-key}") String refreshKey,
            @Value("${jwt.duration}") Long tokenValidityInSeconds,
            @Value("${jwt.refresh-duration}") Long refreshTokenValidityInSeconds,
            @Value("${jwt.issuer}") String issuer) {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        byte[] refreshKeyBytes = Decoders.BASE64.decode(refreshKey);
        this.refreshKey = Keys.hmacShaKeyFor(refreshKeyBytes);
        this.tokenValidityInSeconds = tokenValidityInSeconds;
        this.refreshTokenValidityInSeconds = refreshTokenValidityInSeconds;
        this.issuer = issuer;
    }

    public String createToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = new Date().getTime();
        Date validity = new Date(now + this.tokenValidityInSeconds * 30 * 60 * 1000L);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("auth", authorities)
                .setIssuer(issuer)
                .setIssuedAt(new Date(now))
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createTokenFromMember(Member member) {
        long now = new Date().getTime();
        Date validity = new Date(now + this.tokenValidityInSeconds * 30 * 60 *  1000L);

        return Jwts.builder()
                .setSubject(member.getEmail())
                .claim("auth", "ROLE_" + member.getRole())
                .claim("id", member.getId())
                .setIssuer(issuer)
                .setIssuedAt(new Date(now))
                .setExpiration(validity)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshToken(Authentication authentication) {
        long now = new Date().getTime();
        Date validity = new Date(now + this.refreshTokenValidityInSeconds * 24 * 60 * 60 * 1000L);

        return Jwts.builder()
                .setSubject(authentication.getName())
                .setIssuer(issuer)
                .setIssuedAt(new Date(now))
                .setExpiration(validity)
                .signWith(refreshKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public String createRefreshTokenFromMember(Member member) {
        long now = new Date().getTime();
        Date validity = new Date(now + this.refreshTokenValidityInSeconds * 24 * 60 * 60 * 1000L);

        return Jwts.builder()
                .setSubject(member.getEmail())
                .setIssuer(issuer)
                .setIssuedAt(new Date(now))
                .setExpiration(validity)
                .signWith(refreshKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities;
        Object authClaim = claims.get("auth");

        if (authClaim != null) {
            authorities = Arrays.stream(authClaim.toString().split(","))
                    .map(SimpleGrantedAuthority::new)
                    .toList();
        } else {
            authorities = List.of(new SimpleGrantedAuthority("ROLE_REGULAR")); // 기본 권한 부여
        }

        User principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, token, authorities);
    }

    public boolean validateToken(String token) {
        if (token == null || token.isBlank()) {
            log.warn("JWT 토큰이 비어있습니다.");
            return false;
        }

        try {
            token = token.trim(); // 혹시 모를 공백 제거
            Jwts.parserBuilder()
                    .setSigningKey(key)  // 토큰 생성 시 사용한 동일 키
                    .build()
                    .parseClaimsJws(token);
            return true; // 정상 토큰
        } catch (ExpiredJwtException e) {
            log.warn("JWT 만료됨: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 JWT: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.warn("JWT 포맷 오류: {}", e.getMessage());
        } catch (SignatureException e) {
            log.warn("JWT 서명 불일치: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("JWT가 잘못되었거나 null: {}", e.getMessage());
        }

        return false; // 위 예외 중 하나 발생 시 false 반환
    }

    public boolean validateRefreshToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(refreshKey).build().parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getEmailFromRefreshToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(refreshKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
