package com.asyraaf.assignment.User;

import java.util.List;
import java.util.UUID;

import com.asyraaf.assignment.Auth.dto.RegisterRequest;
import com.asyraaf.assignment.User.dto.UpdateRequest;
import com.asyraaf.assignment.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<List<User>> getAllUsers(@AuthenticationPrincipal String email) {
        List<User> users = userService.getAllByCompany(email);

        return ApiResponse.success(users, "fetched users");
    }

    @GetMapping("/{id}")
    public ApiResponse<User> getUser(@PathVariable UUID id, @AuthenticationPrincipal String email) {
        User user = userService.getByIdForCompany(id, email);

        return ApiResponse.success(user, "fetched user");
    }

    @PutMapping("/{id}")
    public ApiResponse<User> updateUser(@PathVariable UUID id, @RequestBody UpdateRequest request,
            @AuthenticationPrincipal String email) {
        User user = userService.updateUser(id, request, email);

        return ApiResponse.success(user, "successfully updated user");
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteUser(@PathVariable UUID id, @AuthenticationPrincipal String email) {
        userService.deleteUser(id, email);

        return ApiResponse.success(null, "successfully deleted user");
    }
}
