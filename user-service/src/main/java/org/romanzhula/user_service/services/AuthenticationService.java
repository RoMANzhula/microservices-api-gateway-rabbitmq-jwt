package org.romanzhula.user_service.services;

import lombok.RequiredArgsConstructor;
import org.romanzhula.microservices_common.security.implementations.UserDetailsImpl;
import org.romanzhula.microservices_common.security.jwt.CommonJWTService;
import org.romanzhula.user_service.controllers.requests.LoginRequest;
import org.romanzhula.user_service.controllers.requests.RegistrationRequest;
import org.romanzhula.user_service.controllers.responses.AuthResponse;
import org.romanzhula.user_service.models.User;
import org.romanzhula.user_service.models.enums.UserRole;
import org.romanzhula.user_service.repositories.UserRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CommonJWTService jwtService;
    private final AuthenticationManager authenticationManager;
    private final RabbitTemplate rabbitTemplate;


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

        rabbitTemplate.convertAndSend("user-created-queue", newUser);

        var userDetails = new UserDetailsImpl(
                newUser.getId().toString(),
                newUser.getUsername(),
                newUser.getPassword(),
                List.of(newUser.getRole().name())
        );

        var jwtToken = jwtService.generateToken(userDetails);

        return AuthResponse.builder()
                .token(jwtToken)
                .build()
        ;
    }

    @Transactional
    public AuthResponse login(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        // or we can get data about User from DAO
        // User logUser = userRepository.findByUsername(loginRequest.getUsername()).orElseThrow();

        String jwtToken = jwtService.generateToken(userDetails);

        return AuthResponse.builder()
                .token(jwtToken)
                .build()
        ;
    }

}
