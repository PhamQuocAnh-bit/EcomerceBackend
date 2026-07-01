package com.techstore.controller;


import com.techstore.dto.reponse.UserResponse;
import com.techstore.entity.User;
import com.techstore.repository.UserRepository;
import com.techstore.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminController {
    private final UserService userService;
//    @GetMapping("/test")
//    public String test() {
//        return "Admin test endpoint";
//    }
    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }
    @PutMapping("/{id}/block")
    public UserResponse blockUser(@PathVariable Long id) {
        return userService.blockUser(id);
    }

    @PutMapping("/{id}/active")
    public UserResponse activeUser(@PathVariable Long id) {
        return userService.activeUser(id);
    }

}
