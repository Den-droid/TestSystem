package com.example.project.models.repositories;

import com.example.project.models.entities.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, String> {
    User findByUsernameIgnoreCase(String username);
}
