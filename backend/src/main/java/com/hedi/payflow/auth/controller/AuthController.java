package com.hedi.payflow.auth.controller;

import com.hedi.payflow.auth.dto.AuthResponse;
import com.hedi.payflow.auth.dto.LoginRequest;
import com.hedi.payflow.auth.dto.RegisterRequest;
import com.hedi.payflow.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/customer")
    public AuthResponse registerCustomer(@Valid @RequestBody RegisterRequest request) {
        return authService.registerCustomer(request);
    }

    @PostMapping("/register/merchant")
    public AuthResponse registerMerchant(@Valid @RequestBody RegisterRequest request) {
        return authService.registerMerchant(request);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}