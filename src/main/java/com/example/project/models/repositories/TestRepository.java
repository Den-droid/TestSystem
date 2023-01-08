package com.example.project.models.repositories;

import com.example.project.models.entities.Test;
import com.example.project.models.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TestRepository extends CrudRepository<Test, String> {
    List<Test> findTestsByTopicsId(Integer topicId);

    List<Test> findTestsByUsersAssignedUsername(String usersAssignedUsername);

    List<Test> findAllByUserCreated(User user);
}
