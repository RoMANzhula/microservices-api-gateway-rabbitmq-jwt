package org.romanzhula.user_service.services;

import lombok.RequiredArgsConstructor;
import org.romanzhula.user_service.configurations.security.jwt.JWTService;
import org.romanzhula.user_service.controllers.requests.RegistrationRequest;
import org.romanzhula.user_service.controllers.responses.AuthResponse;
import org.romanzhula.user_service.models.User;
import org.romanzhula.user_service.models.enums.UserRole;
import org.romanzhula.user_service.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;


    @Transactional
    public AuthResponse userRegistration(RegistrationRequest registrationRequest) {
        var newUser = User.builder()
                .username(registrationRequest.getUsername())
                .firstName(registrationRequest.getFirstName())
                .lastName(registrationRequest.getLastName())
                .email(registrationRequest.getEmail())
                .phone(registrationRequest.getPhone())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .role(UserRole.USER)
                .build()
        ;

        userRepository.save(newUser);

        var jwtToken = jwtService.generateToken((UserDetails) newUser);

        return AuthResponse.builder()
                .token(jwtToken)
                .build()
        ;
    }

}
