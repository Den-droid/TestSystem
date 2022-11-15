package com.example.project.models.services;

import com.example.project.models.entities.User;

public interface UserService {
    void register(User user);

    User getByUsername(String username);

    User getCurrentLoggedIn();
}
