package com.example.project.models.services;

import com.example.project.dto.auth.RegisterDto;
import com.example.project.models.entities.User;
import com.example.project.models.enums.Role;

import java.util.List;

public interface UserService {
    void register(RegisterDto user);

    User getByUsername(String username);

    User getCurrentLoggedIn();

    List<User> getByUsernameContainsAndUsernamesNotInAndRole(String usernamePart,
                                                             List<String> usernamesNotIn,
                                                             Role role);
}
