package com.asyraaf.assignment.Auth;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.asyraaf.assignment.Auth.dto.AuthResponse;
import com.asyraaf.assignment.Auth.dto.RegisterRequest;
import com.asyraaf.assignment.common.dto.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
   private final AuthService authService;

   @PostMapping("/register")
   public ApiResponse<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
      AuthResponse res = authService.register(request);

      return ApiResponse.success(res, "user registered successfully");
   }
}
