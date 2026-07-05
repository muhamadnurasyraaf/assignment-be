package com.asyraaf.assignment.Auth.dto;

import java.util.UUID;

import com.asyraaf.assignment.User.Role;

public record AuthResponse(UUID id, String username, String email, Role role, String token) {

}
