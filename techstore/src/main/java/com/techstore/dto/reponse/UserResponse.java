package com.techstore.dto.reponse;


import com.techstore.entity.Role;
import com.techstore.enums.UserStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class UserResponse {
    private Long id;
    private String email;
    //private String password;
    private String fullName;
    private String phoneNumber ;
    private String role;
    private UserStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
