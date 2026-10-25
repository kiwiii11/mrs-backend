package com.mrs.service;

import com.mrs.dto.auth.AuthResponse;
import com.mrs.dto.auth.LoginRequest;
import com.mrs.dto.auth.RegisterRequest;
import com.mrs.entity.Role;
import com.mrs.entity.User;
import com.mrs.exception.DuplicateEmailException;
import com.mrs.repository.UserRepository;
import com.mrs.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        if (userRepository.existsByEmail(req.email())) {
            throw new DuplicateEmailException("An account with this email already exists");
        }
        User user = userRepository.save(User.builder()
                .name(req.name())
                .email(req.email())
                .password(passwordEncoder.encode(req.password()))   // BCrypt (NFR1)
                .role(Role.USER)
                .build());
        return toResponse(user);
    }

    public AuthResponse login(LoginRequest req) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(req.email(), req.password()));
        User user = userRepository.findByEmail(req.email()).orElseThrow();
        return toResponse(user);
    }

    private AuthResponse toResponse(User u) {
        return AuthResponse.of(jwtUtil.generateToken(u), u.getId(), u.getName(), u.getEmail(), u.getRole().name());
    }
}
