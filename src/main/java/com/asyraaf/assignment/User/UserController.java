package com.asyraaf.assignment.User;

import com.asyraaf.assignment.Auth.dto.RegisterRequest;
import com.asyraaf.assignment.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/create")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<User> createUser(
            @Valid @RequestBody RegisterRequest request,
            @AuthenticationPrincipal String email) {
        User admin = userService.getByEmail(email);

        User user = userService.createUser(
                request.getUsername(),
                request.getEmail(),
                request.getPassword(),
                request.getRole(),
                admin.getCompany());

        return ApiResponse.success(user, "successfully created user");
    }
}
