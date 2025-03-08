package org.romanzhula.user_service.controllers;

import lombok.RequiredArgsConstructor;
import org.romanzhula.user_service.controllers.responses.UserResponse;
import org.romanzhula.user_service.services.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/all")
    public ResponseEntity<Page<UserResponse>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(userService.getAll(page, size));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{user-id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable("user-id") String userId
    ) {
        return ResponseEntity.ok(userService.getUserByUd(userId));
    }

}
