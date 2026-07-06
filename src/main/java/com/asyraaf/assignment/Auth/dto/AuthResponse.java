package com.asyraaf.assignment.Auth.dto;

import java.util.UUID;

import com.asyraaf.assignment.User.Role;
import com.asyraaf.assignment.User.User;

public record AuthResponse(UUID id, String username, String email, Role role, String token) {
    public static AuthResponse from(User user, String token) {
        return new AuthResponse(user.getId(), user.getUsername(), user.getEmail(), user.getRole(), token);
    }
}
