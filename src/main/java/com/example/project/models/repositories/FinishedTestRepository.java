package com.example.project.models.repositories;

import com.example.project.models.entities.FinishedTest;
import com.example.project.models.entities.Test;
import com.example.project.models.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface FinishedTestRepository extends JpaRepository<FinishedTest, Integer> {
    List<FinishedTest> findFinishedTestsByUser(User user);

    FinishedTest findByUserAndTest(User user, Test test);
}
