package com.example.project.models.repositories;

import com.example.project.models.entities.User;
import com.example.project.models.enums.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.Collection;
import java.util.List;

public interface UserRepository extends CrudRepository<User, String> {
    User findByUsernameIgnoreCase(String username);

    List<User> findAllByUsernameContainsIgnoreCase(String username);

    List<User> findAllByUsernameContainsIgnoreCaseAndUsernameNotIn(String username,
                                                                   Collection<String> usernamesNotIn);

    List<User> findAllByRole(Role role);
}
