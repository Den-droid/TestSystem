package com.example.project.models.services;

import com.example.project.dto.register.RegisterDto;
import com.example.project.dto.user.EditUserDto;
import com.example.project.dto.user.UserDto;
import com.example.project.models.entities.User;
import com.example.project.models.enums.Role;

import java.util.List;

public interface UserService {
    void register(RegisterDto user);

    User getByUsername(String username);

    UserDto getProfile(User user);

    void edit(User user, EditUserDto dto);

    void delete(User user);

    User getCurrentLoggedIn();

    List<User> getByUsernameContainsAndUsernamesNotInAndRole(String usernamePart,
                                                             List<String> usernamesNotIn,
                                                             Role role);
}
