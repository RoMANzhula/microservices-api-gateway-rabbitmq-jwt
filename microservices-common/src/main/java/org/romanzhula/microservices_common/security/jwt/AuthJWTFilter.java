package org.romanzhula.microservices_common.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.romanzhula.microservices_common.security.CustomUserDetailsService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@NoArgsConstructor(force = true)
public class AuthJWTFilter extends OncePerRequestFilter {

    private static final String AUTH_HEADER = "Authorization";
    private static final String BEARER_KEYWORD_IN_HEADER = "Bearer ";

    private final CommonJWTService jwtService;
    private final CustomUserDetailsService userDetailsService;


    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader(AUTH_HEADER);

        if (authHeader == null || !authHeader.contains(BEARER_KEYWORD_IN_HEADER)) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwtToken = authHeader.substring(BEARER_KEYWORD_IN_HEADER.length());

        String usernameFromToken = jwtService.extractUsernameFromToken(jwtToken);

        if (usernameFromToken != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(usernameFromToken);

            if (jwtService.isTokenValid(jwtToken, userDetails)) {

            }
        }
    }

}
