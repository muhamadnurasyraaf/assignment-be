package com.asyraaf.assignment.Auth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.asyraaf.assignment.Auth.dto.AuthResponse;
import com.asyraaf.assignment.Auth.dto.LoginRequest;
import com.asyraaf.assignment.Auth.dto.RegisterRequest;
import com.asyraaf.assignment.common.dto.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
   private final AuthService authService;

   @GetMapping("/health")
   public String health() {
      return "ok";
   }

   @PostMapping("/register")
   public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
      AuthResponse res = authService.register(request);

      return ApiResponse.success(res, "user registered successfully");
   }

   @PostMapping("/login")
   public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
      AuthResponse res = authService.authenticate(request);

      return ApiResponse.success(res, "successfully authenticated user");
   }
}
