package com.example.project.models.repositories;

import com.example.project.models.entities.FinishedTest;
import com.example.project.models.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface FinishedTestRepository extends PagingAndSortingRepository<FinishedTest, Integer> {
    Page<FinishedTest> findFinishedTestsByUser(User user,
                                               Pageable pageable);
}
