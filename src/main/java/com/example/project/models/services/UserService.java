package com.example.project.models.services;

import com.example.project.dto.auth.RegisterDto;
import com.example.project.models.entities.User;

public interface UserService {
    void register(RegisterDto user);

    User getByUsername(String username);

    User getCurrentLoggedIn();
}
