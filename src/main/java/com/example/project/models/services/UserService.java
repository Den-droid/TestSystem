package com.example.project.models.services;

import com.example.project.dto.user.RegisterDto;
import com.example.project.dto.user.EditUserProfileDto;
import com.example.project.dto.user.UserProfileDto;
import com.example.project.models.entities.User;
import com.example.project.models.enums.Role;

import java.util.List;

public interface UserService {
    void register(RegisterDto user);

    User getByUsername(String username);

    UserProfileDto getProfile(User user);

    void edit(User user, EditUserProfileDto dto);

    void delete(User user);

    User getCurrentLoggedIn();

    List<User> getByUsernameContainsAndUsernamesNotInAndRole(String usernamePart,
                                                             List<String> usernamesNotIn,
                                                             Role role);
}
