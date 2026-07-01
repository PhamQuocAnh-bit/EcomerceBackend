package com.techstore.service;

import com.techstore.dto.reponse.AuthResponse;
import com.techstore.dto.reponse.UserResponse;
import com.techstore.dto.request.LoginRequest;
import com.techstore.dto.request.RegisterRequest;
import com.techstore.entity.Role;
import com.techstore.entity.User;
import com.techstore.mapper.UserMapper;
import com.techstore.repository.RoleRepository;
import com.techstore.repository.UserRepository;
import com.techstore.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor

public class AuthService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private  final UserMapper userMapper;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {
        if(userRepository.existsByEmail(request.getEmail())){
            throw new RuntimeException("Email đã tồn tại");
        }
        Role role = roleRepository.findByName("USER")
                .orElseThrow(() -> new RuntimeException("Khong tim thay role USER"));
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phoneNumber(request.getPhone())
                .role(role)
                .build();
        userRepository.save(user);
        UserResponse userRepone = userMapper.toUserResponse(user);
        String token = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .user(userRepone)
                .build();
    }
    public AuthResponse login(LoginRequest request){
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email không tồn tại"));
        if(!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu không đúng");
        }
        UserResponse userRepone = userMapper.toUserResponse(user);
        String token = jwtService.generateToken(user);
        return AuthResponse.builder()
                .token(token)
                .user(userRepone)
                .build();

    }


}
