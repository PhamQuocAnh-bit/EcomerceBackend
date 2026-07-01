package com.techstore.controller;


import com.techstore.dto.reponse.AuthResponse;
import com.techstore.dto.reponse.MeResponse;
import com.techstore.dto.reponse.UserResponse;
import com.techstore.dto.request.LoginRequest;
import com.techstore.dto.request.RegisterRequest;
import com.techstore.entity.User;
import com.techstore.security.CustomUserDetails;
import com.techstore.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.techstore.mapper.UserMapper;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor

public class AuthController {
    private final AuthService authService;
    private final UserMapper userMapper;
    @PostMapping("/register")
    public AuthResponse register(@Valid @RequestBody RegisterRequest request){
        return authService.register(request);
    }
    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
    @GetMapping("/me")
    public MeResponse me(Authentication authentication) {

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        return MeResponse.builder()
                .id(userDetails.getId())
                .email(userDetails.getUsername())
                .fullName(userDetails.getFullName())
                .role(userDetails.getRole())
                .build();
    }

}
