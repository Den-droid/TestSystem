package com.example.project.models.services.impl;

import com.example.project.dto.test.AddTestDto;
import com.example.project.dto.test.TestWalkthroughDto;
import com.example.project.models.entities.CurrentTest;
import com.example.project.models.entities.FinishedTest;
import com.example.project.models.entities.Test;
import com.example.project.models.repositories.CurrentTestRepository;
import com.example.project.models.repositories.FinishedTestRepository;
import com.example.project.models.repositories.TestRepository;
import com.example.project.models.repositories.UserRepository;
import com.example.project.models.services.TestService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class TestServiceImpl implements TestService {
    private TestRepository testRepository;
    private CurrentTestRepository currentTestRepository;
    private FinishedTestRepository finishedTestRepository;
    private UserRepository userRepository;

    @Override
    public void add(String username, AddTestDto dto) {

    }

    @Override
    public void start(String username, String testId) {

    }

    @Override
    public void finish(String username, String testId, TestWalkthroughDto dto) {

    }

    @Override
    public Page<Test> getAssignedToUser(String username, int page, int limit) {
        return testRepository.findTestsByUsersAssignedUsername(
                username, PageRequest.of(page - 1, limit));
    }

    @Override
    public Page<FinishedTest> getFinishedByUser(String username, int page, int limit) {
        return finishedTestRepository.findFinishedTestsByUser(
                userRepository.findByUsernameIgnoreCase(username), PageRequest.of(page - 1, limit)
        );
    }

    @Override
    public Page<CurrentTest> getCurrentOfUser(String username, int page, int limit) {
        return currentTestRepository.findCurrentTestsByUser(
                userRepository.findByUsernameIgnoreCase(username), PageRequest.of(page - 1, limit)
        );
    }

    @Override
    public Test getById(String id) {
        return testRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);
    }
}