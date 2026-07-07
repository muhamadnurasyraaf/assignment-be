package com.asyraaf.assignment.Auth;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.asyraaf.assignment.Auth.dto.AuthResponse;
import com.asyraaf.assignment.Auth.dto.LoginRequest;
import com.asyraaf.assignment.Auth.dto.RegisterRequest;
import com.asyraaf.assignment.User.User;
import com.asyraaf.assignment.User.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final UserService userService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public AuthResponse register(RegisterRequest request) {
        User user = userService.createUser(request.getUsername(), request.getEmail(), request.getPassword(),
                request.getRole(), null);
        String token = jwtUtil.generateToken(user);

        return AuthResponse.from(user, token);
    }

    private boolean isPasswordValid(String password, String hashedPassword) {
        return passwordEncoder.matches(password, hashedPassword);
    }

    public AuthResponse authenticate(LoginRequest request) {
        User user = userService.getByEmail(request.getEmail());

        if (!isPasswordValid(request.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user);

        return AuthResponse.from(user, token);
    }
}
