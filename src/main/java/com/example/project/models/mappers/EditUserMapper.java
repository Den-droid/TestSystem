package com.example.project.models.mappers;

import com.example.project.dto.user.EditUserProfileDto;
import com.example.project.models.entities.User;

public class EditUserMapper {
    private EditUserMapper() {
    }

    public static void map(User user, EditUserProfileDto dto) {
        user.setUsername(dto.getUsername());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
    }
}
