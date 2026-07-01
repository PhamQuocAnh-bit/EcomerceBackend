package com.techstore.controller;


import com.techstore.dto.reponse.UserResponse;
import com.techstore.dto.request.ChangePasswordRequest;
import com.techstore.dto.request.UpdateProfileRequest;
import com.techstore.security.CustomUserDetails;
import com.techstore.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.connector.Response;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    @GetMapping("/profile")
    public UserResponse getMyProfile(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        return userService.getMyProfile(customUserDetails);
    }
    @PutMapping("/update-profile")
    public UserResponse updateProfile(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                      @Valid @RequestBody
                                      UpdateProfileRequest request) {
        return userService.updateProfile(customUserDetails, request);
    }
    @PutMapping("/change-password")
    public String changePassword(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                        @Valid @RequestBody
                                       ChangePasswordRequest request) {
        userService.changePassword(customUserDetails, request);
        return "Đổi Mật Khẩu Thành Công";
    }



}
