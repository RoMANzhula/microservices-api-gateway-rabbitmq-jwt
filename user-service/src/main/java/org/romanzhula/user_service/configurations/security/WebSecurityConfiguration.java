package org.romanzhula.user_service.configurations.security;

import lombok.RequiredArgsConstructor;
import org.romanzhula.user_service.configurations.ApplicationConfiguration;
import org.romanzhula.user_service.configurations.security.cors.CorsAutoConfiguration;
import org.romanzhula.user_service.configurations.security.cors.EnableCORS;
import org.romanzhula.user_service.configurations.security.filters.AuthJWTFilter;
import org.romanzhula.user_service.configurations.security.jwt.AuthEntryPointJwt;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
@EnableCORS
public class WebSecurityConfiguration {

    private final AuthEntryPointJwt authEntryPointJwtUnauthorizedHandler;
    private final ApplicationConfiguration applicationConfiguration;
    private final CorsAutoConfiguration corsAutoConfiguration;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authEntryPointJwtUnauthorizedHandler)
                )
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        .requestMatchers(
                                "/swagger-ui.html",
                                        "/swagger-ui/**",
                                        "/swagger-resources/**",
                                        "/swagger-resources",
                                        "/v3/api-docs/**",
                                        "/proxy/**"
                                ).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .httpBasic(withDefaults())
                .authenticationProvider(applicationConfiguration.daoAuthenticationProvider())
                .addFilterBefore(applicationConfiguration.authJWTFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(corsAutoConfiguration.corsFilter(), AuthJWTFilter.class)
                .headers((headers) ->
                        headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
                )
        ;

        return httpSecurity.build();
    }

}
