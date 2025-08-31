package com.sharedexpenses.app.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.sharedexpenses.app.dto.AuthResponse;
import com.sharedexpenses.app.entity.User;
import com.sharedexpenses.app.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;


@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

public void register(String name, String email, String password) {
    if (userRepository.findByEmail(email).isPresent()) {
        throw new RuntimeException("Email já cadastrado");
    }

    User user = new User();
    user.setName(name);
    user.setEmail(email);
    user.setPassword(passwordEncoder.encode(password));
    userRepository.save(user);
}

    public AuthResponse login(String email, String senha) {
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(email, senha)
        );

        String token = jwtService.generateToken(email);
        return new AuthResponse(token);
    }

    /**
     * Gera token JWT para um email específico (usado após registro)
     */
    public String generateTokenForEmail(String email) {
        return jwtService.generateToken(email);
    }
}

