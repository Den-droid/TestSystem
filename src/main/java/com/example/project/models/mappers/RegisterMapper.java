package com.example.project.models.mappers;

import com.example.project.dto.register.RegisterDto;
import com.example.project.models.entities.User;
import com.example.project.models.enums.Role;

public class RegisterMapper {
    private RegisterMapper() {
    }

    public static User map(RegisterDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setFirstName(dto.getFirstName());
        user.setEmail(dto.getEmail());
        user.setLastName(dto.getLastName());
        user.setRole(Role.USER);
        user.setActive(true);
        return user;
    }
}
