package com.example.project.config;

import com.example.project.models.entities.User;
import com.example.project.models.enums.Role;
import com.example.project.models.repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.UUID;

@Component
public class InitialConfig {
    private final UserRepository userRepository;

    InitialConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    private void createDefaultUser() {
        if (userRepository.findByUsernameIgnoreCase("admin1") == null) {
            User user = new User();
            user.setUsername("admin1");
            user.setPassword(new BCryptPasswordEncoder()
                    .encode(UUID.randomUUID().toString()));
            user.setFirstName("Admin");
            user.setEmail("admin@gmail.com");
            user.setLastName("Admin");
            user.setRole(Role.ADMIN);
            user.setActive(true);
            userRepository.save(user);
        }
    }
}
