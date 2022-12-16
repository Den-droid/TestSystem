package com.example.project.models.mappers;

import com.example.project.dto.user.EditUserDto;
import com.example.project.models.entities.User;

public class EditUserMapper {
    public static void map(User user, EditUserDto dto) {
        user.setUsername(dto.getUsername());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail());
    }
}
