package com.hedi.payflow.auth.service;

import com.hedi.payflow.auth.dto.AuthResponse;
import com.hedi.payflow.auth.dto.LoginRequest;
import com.hedi.payflow.auth.dto.RegisterRequest;
import com.hedi.payflow.user.entity.Role;
import com.hedi.payflow.user.entity.User;
import com.hedi.payflow.user.entity.UserStatus;
import com.hedi.payflow.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public AuthResponse registerCustomer(RegisterRequest request) {
        return register(request, Role.CUSTOMER);
    }

    public AuthResponse registerMerchant(RegisterRequest request) {
        return register(request, Role.MERCHANT);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        return new AuthResponse(
                "temporary-token",
                user.getId(),
                user.getEmail(),
                user.getRole()
        );
    }

    private AuthResponse register(RegisterRequest request, Role role) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(request.getPassword())
                .role(role)
                .status(UserStatus.ACTIVE)
                .build();

        User savedUser = userRepository.save(user);

        return new AuthResponse(
                "temporary-token",
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getRole()
        );
    }
}