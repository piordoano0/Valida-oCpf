package com.exemplo.validacaocpf.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.List;

@Component
public class JwtTokenProvider {

    private final String jwtSecret = "chave-secreta-muito-segura-chave-secreta-muito-segura"; // Pelo menos 256 bits
                                                                                              // para HS256
    private final long jwtExpirationMs = 86400000; // 1 dia

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    public String generateToken(String username, List<String> roles) {
        return Jwts.builder()
                .setSubject(username)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // ✅ Nova forma
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey()) // ✅ Nova forma
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            // Token inválido
            return false;
        }
    }

    public String getUsernameFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey()) // ✅ Nova forma
                .build()
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    @SuppressWarnings("")
    public List<String> getRolesFromJWT(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

        Object rolesObject = claims.get("roles");
        if (rolesObject instanceof List<?>) {
            return ((List<?>) rolesObject).stream()
                    .map(Object::toString)
                    .toList(); // Converte com segurança cada item para String
        }
        return List.of(); // Retorna lista vazia se não for lista
    }

}
