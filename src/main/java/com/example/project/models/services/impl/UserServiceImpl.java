package com.example.project.models.services.impl;

import com.example.project.models.entities.User;
import com.example.project.models.repositories.UserRepository;
import com.example.project.models.services.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void register(User user) {
        User check = userRepository.findByUsernameIgnoreCase(user.getUsername());
        if (check != null)
            throw new IllegalArgumentException("There is already user with such username existed!!!");
        userRepository.save(user);
    }

    @Override
    public User getByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameIgnoreCase(username);
        if (user == null)
            throw new UsernameNotFoundException("There is no user with username " + username + "!!!");
        return user;
    }

    @Override
    public User getCurrentLoggedIn() {
        Object userObject = SecurityContextHolder.getContext().getAuthentication().getDetails();
        if (userObject instanceof UserDetails) {
            return userRepository.findByUsernameIgnoreCase(((UserDetails) userObject).getUsername());
        }
        return null;
    }
}
