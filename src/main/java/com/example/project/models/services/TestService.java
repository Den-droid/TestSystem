package com.example.project.models.services;

import com.example.project.dto.test.AddTestDto;
import com.example.project.dto.test.TestWalkthroughDto;
import com.example.project.models.entities.CurrentTest;
import com.example.project.models.entities.FinishedTest;
import com.example.project.models.entities.Test;
import org.springframework.data.domain.Page;

public interface TestService {
    void add(String username, AddTestDto dto);

    void start(String username, String testId);

    void finish(String username, String testId, TestWalkthroughDto dto);

    Page<Test> getAssignedToUser(String username, int page, int limit);

    Page<FinishedTest> getFinishedByUser(String username, int page, int limit);

    Page<CurrentTest> getCurrentOfUser(String username, int page, int limit);

    Test getById(String id);
}
