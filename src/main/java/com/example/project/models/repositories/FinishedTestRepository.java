package com.example.project.models.repositories;

import com.example.project.models.entities.FinishedTest;
import com.example.project.models.entities.Test;
import com.example.project.models.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FinishedTestRepository extends CrudRepository<FinishedTest, Integer> {
    List<FinishedTest> findFinishedTestsByUser(User user);

    FinishedTest findByUserAndTest(User user,
                                   Test test);
}
