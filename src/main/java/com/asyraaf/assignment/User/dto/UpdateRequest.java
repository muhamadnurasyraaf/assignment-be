package com.asyraaf.assignment.User.dto;

import com.asyraaf.assignment.User.Role;

import lombok.Getter;

@Getter
public class UpdateRequest {
    private String username;

    private Role role;
}
