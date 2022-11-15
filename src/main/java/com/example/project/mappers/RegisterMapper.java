package com.example.project.mappers;

import com.example.project.dto.auth.RegisterDto;
import com.example.project.models.entities.User;
import com.example.project.models.enums.Role;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class RegisterMapper {
    public static User map(RegisterDto dto) {
        User user = new User();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        user.setUsername(dto.getUsername());
        user.setPassword(encoder.encode(user.getPassword()));
        user.setFirstName(dto.getFirstName());
        user.setEmail(dto.getEmail());
        user.setLastName(dto.getLastName());
        user.setRole(Role.USER);
        user.setActive(true);
        return user;
    }
}
