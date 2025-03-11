package org.romanzhula.user_service.configurations.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.function.Function;

@Service
public class JWTService {

    @Value("${app.jwt_secret_code}")
    private String jwtSecretCode;

    public String generateToken(UserDetails userDetails) {
        Instant issuedAt = LocalDateTime.now().toInstant(ZoneOffset.UTC);
        Instant expiration = issuedAt.plus(2, ChronoUnit.HOURS);

        return Jwts
                .builder()
                .subject(userDetails.getUsername())
                .issuedAt(Date.from(issuedAt))
                .expiration(Date.from(expiration))
                .signWith(getSecretKey())
                .compact()
        ;
    }

    private SecretKey getSecretKey() {
        return Keys.hmacShaKeyFor(jwtSecretCode.getBytes());
    }

    public String extractUsernameFromToken(String jwtToken) {
        return extractClaim(jwtToken, Claims::getSubject);
    }

    public <T> T extractClaim(String jwtToken, Function<Claims, T> function) {
        Claims claims = extractAllClaims(jwtToken);

        return function.apply(claims);
    }

    public Claims extractAllClaims(String jwtToken) {
        return Jwts
                .parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload()
        ;
    }


    public boolean isTokenValid(String jwtToken, UserDetails userDetails) {
        var usernameFromToken = extractUsernameFromToken(jwtToken);
        
        return usernameFromToken.equals(userDetails.getUsername()) && !isTokenExpired(jwtToken);
    }

    private boolean isTokenExpired(String jwtToken) {
        return extractExpiration(jwtToken).before(new Date());
    }

    private Date extractExpiration(String jwtToken) {
        return extractClaim(jwtToken, Claims::getExpiration);
    }

}
