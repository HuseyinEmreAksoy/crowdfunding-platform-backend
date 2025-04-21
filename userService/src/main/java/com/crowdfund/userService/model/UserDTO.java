package com.crowdfund.userService.model;

import com.crowdfund.userService.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

    private Long id;
    private String username;
    private String email;
    private String password;
    private String ethereumAddress;
    private LocalDateTime createdAt = LocalDateTime.now();


    public static UserDTO fromEntity(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .email(user.getEmail())
                .ethereumAddress(user.getEthereumAddress())
                .createdAt(user.getCreatedAt())
                .build();
    }

}
