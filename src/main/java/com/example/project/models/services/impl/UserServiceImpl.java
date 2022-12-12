package com.example.project.models.services.impl;

import com.example.project.dto.auth.RegisterDto;
import com.example.project.models.enums.Role;
import com.example.project.models.mappers.RegisterMapper;
import com.example.project.models.entities.User;
import com.example.project.models.repositories.UserRepository;
import com.example.project.models.services.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void register(RegisterDto dto) {
        User user = RegisterMapper.map(dto);
        User check = userRepository.findByUsernameIgnoreCase(user.getUsername());
        if (check != null)
            throw new IllegalArgumentException("There is already user with such username existed!!!");
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
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
        Object userObject = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (userObject instanceof UserDetails) {
            return userRepository.findByUsernameIgnoreCase(((UserDetails) userObject).getUsername());
        }
        return null;
    }

    @Override
    public List<User> getByUsernameContainsAndUsernamesNotInAndRole(String usernamePart,
                                                                    List<String> usernamesNotIn,
                                                                    Role role) {
        List<User> users;
        if (usernamesNotIn == null || usernamesNotIn.size() == 0)
            users = userRepository.findAllByUsernameContainsIgnoreCase(usernamePart);
        else
            users = userRepository.findAllByUsernameContainsIgnoreCaseAndUsernameNotIn
                    (usernamePart, usernamesNotIn);
        return users.stream()
                .filter(x -> x.getRole().equals(Role.USER))
                .collect(Collectors.toList());
    }
}
