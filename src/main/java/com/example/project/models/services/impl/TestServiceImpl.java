package com.example.project.models.services.impl;

import com.example.project.dto.page.PageDto;
import com.example.project.dto.test.AddTestDto;
import com.example.project.dto.test.TestWalkthroughDto;
import com.example.project.models.entities.*;
import com.example.project.models.enums.TestDifficulty;
import com.example.project.models.enums.TestType;
import com.example.project.models.repositories.CurrentTestRepository;
import com.example.project.models.repositories.FinishedTestRepository;
import com.example.project.models.repositories.TestRepository;
import com.example.project.models.repositories.UserRepository;
import com.example.project.models.services.TestService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class TestServiceImpl implements TestService {
    private final TestRepository testRepository;
    private final CurrentTestRepository currentTestRepository;
    private final FinishedTestRepository finishedTestRepository;
    private final UserRepository userRepository;

    public TestServiceImpl(TestRepository testRepository,
                           CurrentTestRepository currentTestRepository,
                           FinishedTestRepository finishedTestRepository,
                           UserRepository userRepository) {
        this.finishedTestRepository = finishedTestRepository;
        this.testRepository = testRepository;
        this.currentTestRepository = currentTestRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void add(User user, AddTestDto dto) {

    }

    @Override
    public void start(User user, String testId) {
        CurrentTest currentTest = new CurrentTest();
        currentTest.setUser(user);
        currentTest.setTest(testRepository.findById(testId)
                .orElseThrow(NoSuchElementException::new));
        currentTest.setDateStarted(LocalDateTime.now());
        currentTestRepository.save(currentTest);
    }

    @Override
    public void finish(User user, String testId, TestWalkthroughDto dto) {

    }


    @Override
    public PageDto<Test> getByUser(String type, String username, int page, int limit) {
        TestType testType = TestType.getByText(type);
        switch (testType) {
            case ALL:
                Page<Test> tests = getAssignedToUserByUsername(username, page, limit);
                List<Test> testList = tests.getContent();
                return new PageDto<>(testList, page, tests.getTotalPages());
            case FINISHED:
                Page<CurrentTest> currentTests = getCurrentOfUserByUsername(username, page, limit);
                List<Test> currentTestsList = currentTests.getContent().stream()
                        .map(CurrentTest::getTest)
                        .collect(Collectors.toList());
                return new PageDto<>(currentTestsList, page, currentTests.getTotalPages());
            case CURRENT:
                Page<FinishedTest> finishedTests = getFinishedByUsername(username, page, limit);
                List<Test> finishedTestsList = finishedTests.getContent().stream()
                        .map(FinishedTest::getTest)
                        .collect(Collectors.toList());
                return new PageDto<>(finishedTestsList, page, finishedTests.getTotalPages());
            case CREATED:
                Page<Test> userCreatedTest = getCreatedByUser(username, page, limit);
                List<Test> userCreatedTestsList = userCreatedTest.getContent();
                return new PageDto<>(userCreatedTestsList, page, userCreatedTest.getTotalPages());
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public Test getById(String id) {
        return testRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);
    }

    @Override
    public boolean canWalkthrough(User user, String testId) {
        Test test = testRepository.findById(testId).orElseThrow(NoSuchElementException::new);
        Set<User> usersAssigned = test.getUsersAssigned();
        return usersAssigned.contains(user);
    }

    @Override
    public List<Question> getTestQuestions(Test test) {
        return null;
    }

    @Override
    public Set<Topic> getTestTopics(Test test) {
        return test.getTopics();
    }

    @Override
    public List<String> getTestDifficulties() {
        return Arrays.stream(TestDifficulty.values())
                .map(TestDifficulty::getText)
                .collect(Collectors.toList());
    }

    private Page<Test> getAssignedToUserByUsername(String username, int page, int limit) {
        return testRepository.findTestsByUsersAssignedUsername(
                username, PageRequest.of(page - 1, limit));
    }

    private Page<FinishedTest> getFinishedByUsername(String username, int page, int limit) {
        return finishedTestRepository.findFinishedTestsByUser(
                userRepository.findByUsernameIgnoreCase(username), PageRequest.of(page - 1, limit)
        );
    }

    private Page<CurrentTest> getCurrentOfUserByUsername(String username, int page, int limit) {
        return currentTestRepository.findCurrentTestsByUser(
                userRepository.findByUsernameIgnoreCase(username), PageRequest.of(page - 1, limit)
        );
    }

    private Page<Test> getCreatedByUser(String username, int page, int limit) {
        return testRepository.findAllByUserCreated(
                userRepository.findByUsernameIgnoreCase(username), PageRequest.of(page - 1, limit));
    }
}