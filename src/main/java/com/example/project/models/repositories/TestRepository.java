package com.example.project.models.repositories;

import com.example.project.models.entities.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface TestRepository extends PagingAndSortingRepository<Test, String> {
    List<Test> findTestsByTopicsId(Integer topics_id);

    Page<Test> findTestsByUsersAssignedUsername(String usersAssigned_username,
                                                Pageable pageable);
}
