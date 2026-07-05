package com.asyraaf.assignment.Auth;

import org.springframework.stereotype.Service;

import com.asyraaf.assignment.Auth.dto.AuthResponse;
import com.asyraaf.assignment.Auth.dto.RegisterRequest;
import com.asyraaf.assignment.User.User;
import com.asyraaf.assignment.User.UserService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class AuthService {
    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthResponse register(RegisterRequest request) {
        User user = userService.createUser(request.getUsername(), request.getEmail(), request.getPassword(),
                request.getRole());
        String token = jwtUtil.generateToken(user);

        return new AuthResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole(), token);
    }
}
