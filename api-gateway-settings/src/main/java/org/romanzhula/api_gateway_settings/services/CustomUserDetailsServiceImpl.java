package org.romanzhula.api_gateway_settings.services;

import org.romanzhula.api_gateway_settings.dto.UserResponse;
import org.romanzhula.api_gateway_settings.utils.UserServiceFeignClient;
import org.romanzhula.microservices_common.security.CustomUserDetailsService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsServiceImpl implements CustomUserDetailsService {

    private final UserServiceFeignClient userServiceFeignClient;

    public CustomUserDetailsServiceImpl(@Lazy UserServiceFeignClient userServiceFeignClient) {
        this.userServiceFeignClient = userServiceFeignClient;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserResponse userResponse = userServiceFeignClient.getUserByUsername(username);

        if (userResponse == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        return User
                .withUsername(userResponse.getUsername())
                .password(userResponse.getPassword())
                .roles(userResponse.getRoles().toArray(new String[0]))
                .build()
        ;
    }

}
