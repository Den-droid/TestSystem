package com.example.project.models.repositories;

import com.example.project.models.entities.Test;
import com.example.project.models.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestRepository extends JpaRepository<Test, String> {
    List<Test> findTestsByTopicsId(Integer topics_id);

    List<Test> findTestsByUsersAssignedUsername(String usersAssigned_username);

    List<Test> findAllByUserCreated(User user);
}
