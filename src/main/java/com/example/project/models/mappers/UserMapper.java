package com.example.project.models.mappers;

import com.example.project.dto.user.UserDto;
import com.example.project.models.entities.User;

public class UserMapper {
    private UserMapper() {
    }

    public static UserDto map(User user) {
        UserDto dto = new UserDto();
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setUsername(user.getUsername());
        return dto;
    }
}
