package  com.techstore.mapper;


import com.techstore.dto.reponse.UserResponse;
import com.techstore.entity.User;
import org.springframework.stereotype.Component;

//import org.springframework.security.core.userdetails.User;
@Component
public class UserMapper {
    public UserResponse toUserResponse(User user) {
       return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .phoneNumber(user.getPhoneNumber())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .role(user.getRole().getName())
                .build();



    }

}