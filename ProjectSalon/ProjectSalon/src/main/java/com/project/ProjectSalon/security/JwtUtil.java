// JwtUtil.java
package com.project.ProjectSalon.security;

import com.project.ProjectSalon.entity.Users;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    /** 24 h */
    private static final long JWT_TOKEN_VALIDITY = 24 * 60 * 60 * 1000L;

    /* ───── token ────────────────────────────────────────────────────────── */
    public String generateToken(UserDetails ud) {

        Users user = (Users) ud;       // we know our UserDetails impl
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());   //  ←  "CUSTOMER", "ADMIN", …
        claims.put("uid" , user.getUserId());        //  ←  handy on the client

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())      // e‑mail
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(getKey()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public String getRoleFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(getKey()).build()
                .parseClaimsJws(token).getBody().get("role", String.class);
    }

    public Long getUidFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(getKey()).build()
                .parseClaimsJws(token).getBody().get("uid", Long.class);
    }

    public boolean validateToken(String token, UserDetails ud) {
        try {
            return getUsernameFromToken(token).equals(ud.getUsername()) &&
                    new Date().before(Jwts.parserBuilder()
                            .setSigningKey(getKey()).build()
                            .parseClaimsJws(token).getBody().getExpiration());
        } catch (Exception e) { return false; }
    }

    /* ─ helpers ─ */
    private Key getKey() { return Keys.hmacShaKeyFor(secret.getBytes()); }
}
