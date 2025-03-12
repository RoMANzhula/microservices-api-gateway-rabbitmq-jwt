package org.romanzhula.user_service.configurations.security.implementations;

import lombok.RequiredArgsConstructor;
import org.romanzhula.microservices_common.security.implementations.UserDetailsImpl;
import org.romanzhula.user_service.models.User;
import org.romanzhula.user_service.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username))
        ;

        return UserDetailsImpl.build(
                String.valueOf(user.getId()),
                user.getUsername(),
                user.getPassword(),
                List.of(user.getRole().name())
        );
    }

}
