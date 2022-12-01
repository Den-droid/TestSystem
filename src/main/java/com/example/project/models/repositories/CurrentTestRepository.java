package com.example.project.models.repositories;

import com.example.project.models.entities.CurrentTest;
import com.example.project.models.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CurrentTestRepository extends PagingAndSortingRepository<CurrentTest, Integer> {
    Page<CurrentTest> findCurrentTestsByUser(User user, Pageable pageable);
}
