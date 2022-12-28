package com.example.project.models.services;

import com.example.project.dto.page.PageDto;
import com.example.project.dto.test.*;
import com.example.project.models.entities.Test;
import com.example.project.models.entities.Topic;
import com.example.project.models.entities.User;

import java.time.LocalTime;
import java.util.List;
import java.util.Set;

public interface TestService {
    void add(User user, AddTestDto dto);

    void saveAnswer(User user, String testId, TestWalkthroughDto testWalkthroughDto);

    void start(User user, String testId);

    void finish(User user, String testId);

    PageDto<Test> getPage(String type, String name, User user, int page, int limit);

    Test getById(String id);

    boolean canWalkthrough(User user, String testId);

    LocalTime getTimeLeft(User user, String testId);

    boolean isTestOutdated(String testId);

    boolean isTimeUnlimited(String testId);

    boolean isTestTooEarly(String testId);

    boolean hasStarted(User user, String testId);

    boolean hasFinished(User user, String testId);

    boolean isUserCreated(String testId, User user);

    TestQuestionDto getTestQuestionByNumber(String testId, int number);

    TestAnswerDto getTestQuestionAnswerByUserAndNumber(User user, String testId, int number);

    TestDto getIntro(Test test);

    TestResultInfoDto getResultInfo(String testId);

    TestResultDto getUserTestResult(String testId, User user);

    List<String> getTestDifficulties();

    List<String> getTestTypes();
}
