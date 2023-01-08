package com.example.project.models.mappers;

import com.example.project.dto.user.UserProfileDto;
import com.example.project.models.entities.User;

public class UserProfileMapper {
    private UserProfileMapper() {
    }

    public static UserProfileDto map(User user) {
        UserProfileDto dto = new UserProfileDto();
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setUsername(user.getUsername());
        return dto;
    }
}
