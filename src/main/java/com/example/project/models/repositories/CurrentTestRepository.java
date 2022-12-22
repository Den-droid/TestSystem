package com.example.project.models.repositories;

import com.example.project.models.entities.CurrentTest;
import com.example.project.models.entities.Test;
import com.example.project.models.entities.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CurrentTestRepository extends JpaRepository<CurrentTest, Integer> {
    List<CurrentTest> findCurrentTestsByUser(User user);

    CurrentTest findByUserAndTest(User user, Test test);
}
