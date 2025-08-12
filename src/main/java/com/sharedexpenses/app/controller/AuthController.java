package com.sharedexpenses.app.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sharedexpenses.app.dto.AuthRequest;
import com.sharedexpenses.app.dto.AuthResponse;
import com.sharedexpenses.app.dto.RegisterRequest;
import com.sharedexpenses.app.service.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        authService.register(request.getName(), request.getEmail(), request.getPassword());
        AuthResponse response = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request.getEmail(), request.getPassword());
        return ResponseEntity.ok(response);
    }
}

