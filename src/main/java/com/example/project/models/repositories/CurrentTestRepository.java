package com.example.project.models.repositories;

import com.example.project.models.entities.CurrentTest;
import com.example.project.models.entities.Test;
import com.example.project.models.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CurrentTestRepository extends CrudRepository<CurrentTest, Integer> {
    List<CurrentTest> findCurrentTestsByUser(User user);

    CurrentTest findByUserAndTest(User user,
                                  Test test);
}
