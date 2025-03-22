package org.romanzhula.api_gateway_settings.configurations;


import lombok.RequiredArgsConstructor;
import org.romanzhula.api_gateway_settings.services.CustomUserDetailsServiceImpl;
import org.romanzhula.microservices_common.cors.CorsAutoConfiguration;
import org.romanzhula.microservices_common.cors.CorsConfigurationProperties;
import org.romanzhula.microservices_common.security.jwt.AuthEntryPointJwt;
import org.romanzhula.microservices_common.security.jwt.AuthJWTFilter;
import org.romanzhula.microservices_common.security.jwt.CommonJWTService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerResponse;

import static org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions.http;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {

    @Value("${app.jwt_secret_code}")
    private String secretKey;

    private final CustomUserDetailsServiceImpl userDetailServiceImpl;

                            // ROUTES
    @Bean
    public RouterFunction<ServerResponse> userRoutes() {
        return GatewayRouterFunctions.route("user-service-route")
                .GET("/api/v1/users/**", http("http://localhost:8081"))
                .POST("/api/v1/auth", http("http://localhost:8081"))
                .build();
    }




                            // COMMON CONFIGURATIONS
    @Bean
    public AuthJWTFilter authJWTFilter(CommonJWTService jwtService, CustomUserDetailsServiceImpl userDetailsService) {
        return new AuthJWTFilter(jwtService, userDetailsService);
    }

    @Bean
    public AuthEntryPointJwt authEntryPointJwt() {
        return new AuthEntryPointJwt();
    }

    @Bean
    public CommonJWTService commonJWTService() {
        return new CommonJWTService(secretKey);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(8);
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();

        daoAuthenticationProvider.setUserDetailsService(userDetailServiceImpl);
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());

        return daoAuthenticationProvider;
    }

    @Bean
    public CorsAutoConfiguration corsAutoConfiguration() {
        return new CorsAutoConfiguration(properties());
    }

    @Bean
    public CorsConfigurationProperties properties() {
        return new CorsConfigurationProperties();
    }

}
