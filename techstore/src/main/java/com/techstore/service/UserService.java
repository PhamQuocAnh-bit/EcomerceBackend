package com.techstore.service;

import com.techstore.dto.reponse.UserResponse;
import com.techstore.dto.request.ChangePasswordRequest;
import com.techstore.dto.request.UpdateProfileRequest;
import com.techstore.entity.User;
import com.techstore.enums.UserStatus;
import com.techstore.mapper.UserMapper;
import com.techstore.repository.UserRepository;
import com.techstore.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserResponse getMyProfile(CustomUserDetails customUserDetails) {
        User user = userRepository.findByEmailWithRole(customUserDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Khong tim thay user"));
        return userMapper.toUserResponse(user);
    }

    public UserResponse updateProfile(CustomUserDetails customerUserDetail, UpdateProfileRequest request) {
        User user = userRepository.findByEmailWithRole(customerUserDetail.getUsername())
                .orElseThrow(() -> new RuntimeException("Khong tim thay user"));
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());
        userRepository.save(user);
        return userMapper.toUserResponse(user);

    }
    public void changePassword(CustomUserDetails customUserDetails, ChangePasswordRequest request) {
        User user = userRepository.findByEmailWithRole(customUserDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("Khong tim thay user"));
        if(!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu cũ không đúng");
        }
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
    // admin
    public List<UserResponse> getAllUsers(){
        return userRepository.findAll().stream()
                .map(userMapper::toUserResponse)
                .toList();
    }

    public UserResponse blockUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khong tim thay user"));
        user.setStatus(UserStatus.BLOCKED);
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }

    public UserResponse activeUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khong tim thay user"));
        user.setStatus(UserStatus.ACTIVE);
        userRepository.save(user);
        return userMapper.toUserResponse(user);
    }




}
