package org.romanzhula.user_service.controllers;

import lombok.RequiredArgsConstructor;
import org.romanzhula.user_service.controllers.requests.RegistrationRequest;
import org.romanzhula.user_service.controllers.responses.AuthResponse;
import org.romanzhula.user_service.services.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @PatchMapping("/register")
    public ResponseEntity<AuthResponse> registration(
            @RequestBody RegistrationRequest registrationRequest
    ) {
        return ResponseEntity.ok(authenticationService.userRegistration(registrationRequest));
    }

}
