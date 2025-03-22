package org.romanzhula.api_gateway_settings.configurations;

import lombok.RequiredArgsConstructor;
import org.romanzhula.microservices_common.cors.CorsAutoConfiguration;
import org.romanzhula.microservices_common.cors.EnableCORS;
import org.romanzhula.microservices_common.security.jwt.AuthEntryPointJwt;
import org.romanzhula.microservices_common.security.jwt.AuthJWTFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
@EnableCORS
public class SecurityConfiguration {

    private final AuthEntryPointJwt authEntryPointJwtUnauthorizedHandler;
    private final CorsAutoConfiguration corsAutoConfiguration;
    private final AuthJWTFilter authJwtFilter;
    private final DaoAuthenticationProvider daoAuthenticationProvider;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authEntryPointJwtUnauthorizedHandler)
                )
                .authorizeHttpRequests(request -> request
                        .requestMatchers(
                                "/api/v1/auth/**",
                                "/img/**",
                                "/css/**",
                                "/js/**"
                        ).permitAll()
                        .anyRequest().permitAll()
                )
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .httpBasic(withDefaults())
                .authenticationProvider(daoAuthenticationProvider)
                .addFilterBefore(authJwtFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(corsAutoConfiguration.corsFilter(), AuthJWTFilter.class)
                .headers((headers) ->
                        headers
                                .frameOptions((frameOptions) -> frameOptions.disable())
                )
        ;

        return httpSecurity.build();
    }

}
