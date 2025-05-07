package com.example.auth_service.service;

import com.example.auth_service.constant.JwtConstant;
import com.example.auth_service.dto.response.JwtErrorCode;
import com.example.auth_service.dto.response.JwtValidationResult;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class JwtService {
    private final SecretKey key = Keys.hmacShaKeyFor(JwtConstant.SECRET_KEY.getBytes());
    private final long jwtExpiration = JwtConstant.jwtExpiration;

    public String generateToken(Authentication authentication) {
        log.info("Generating token: {}", authentication.getName());
        List<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration((new Date(new Date().getTime() + jwtExpiration)))
                .setSubject(authentication.getName())
                .claim("authorities", roles)
                .signWith(key)
                .compact();
    }

    public JwtValidationResult validateToken(String token) {
        log.info("Validating token: {}", token);
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
            return JwtValidationResult.success(claims, JwtErrorCode.VALID_TOKEN);
        } catch (ExpiredJwtException e) {
            log.error("JWT expired: {}", e.getMessage());
            return JwtValidationResult.failure(JwtErrorCode.EXPIRED_TOKEN);
        } catch (MalformedJwtException e) {
            log.error("JWT malformed: {}", e.getMessage());
            return JwtValidationResult.failure(JwtErrorCode.MALFORMED_TOKEN);
        } catch (SignatureException e) {
            log.error("JWT signature invalid: {}", e.getMessage());
            return JwtValidationResult.failure(JwtErrorCode.INVALID_SIGNATURE);
        } catch (UnsupportedJwtException e) {
            log.error("JWT unsupported: {}", e.getMessage());
            return JwtValidationResult.failure(JwtErrorCode.UNSUPPORTED_TOKEN);
        } catch (IllegalArgumentException e) {
            log.error("JWT claim error: {}", e.getMessage());
            return JwtValidationResult.failure(JwtErrorCode.CLAIM_ERROR);
        }
    }


    public String getEmailFromJwt(String token) {
        log.info("Token: {}", token);
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = getEmailFromJwt(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsTFunction) {
        final Claims claims = extractAllClaims(token);
        return claimsTFunction.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new RuntimeException("Unable to extract claims", e);
        }
    }
}
