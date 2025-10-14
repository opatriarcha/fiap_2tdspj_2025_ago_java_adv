package br.com.fiap.tds._tdsq.Library.infrastructure.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class JwtHelper {

    private final String SECRET = "CHAVE_SECRETA_SUPER_FUCKER_SEGURA_PACARAI";
    private final int TOKEN_EXPIRATION_MS = 24 * 60 * 60 * 1000;  // 24 horas
    private final int REFRESH_TOKEN_EXPIRATION_MS = 7 * 24 * 60 * 60 * 1000; // 7 dias

    // Geração do token
    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRATION_MS))
                .signWith(getSignKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    // Extração do nome de usuário do token
    public String extractUsername(String token) {
        return Jwts.parser()  // Método correto após v0.12.x
                .setSigningKey(getSignKey())  // Chave usada para validar a assinatura
                .build()
                .parseClaimsJws(token)  // Parse do token JWT
                .getBody()
                .getSubject();
    }

    // Obtendo a chave secreta para assinatura
    private Key getSignKey() {
        return Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));
    }

    // Verificando se o token expirou
    public boolean isTokenExpired(String token) {
        Date expirationDate = Jwts.parser()  // Método correto após v0.12.x
                .setSigningKey(getSignKey())  // Chave usada para validar a assinatura
                .build()
                .parseClaimsJws(token)  // Parse do token JWT
                .getBody()
                .getExpiration();  // Data de expiração

        return expirationDate.before(new Date());  // Verifica se a data de expiração já passou
    }

    // Geração de um refresh token
    public String generateRefreshToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_MS))
                .signWith(getSignKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }
}
