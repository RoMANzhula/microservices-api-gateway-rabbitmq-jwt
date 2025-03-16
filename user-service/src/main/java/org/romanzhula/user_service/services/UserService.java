package org.romanzhula.user_service.services;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.romanzhula.user_service.controllers.responses.UserFeignResponse;
import org.romanzhula.user_service.controllers.responses.UserResponse;
import org.romanzhula.user_service.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<UserResponse> getAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return userRepository.findAll(pageable)
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getPhone()
                ))
        ;
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(String userId) {
        return userRepository.findById(UUID.fromString(userId))
                .map(user -> new UserResponse(
                        user.getId(),
                        user.getUsername(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getPhone()
                ))
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId))
        ;
    }

    @Transactional(readOnly = true)
    public UserFeignResponse getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(user -> new UserFeignResponse(
                        user.getId().toString(),
                        user.getUsername(),
                        user.getPassword(),
                        Collections.singleton(user.getRole().toString())
                ))
                .orElseThrow(() -> new EntityNotFoundException("User not found with username: " + username))
        ;
    }

}
