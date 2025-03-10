package org.romanzhula.user_service.configurations.security.jwt;

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

}
