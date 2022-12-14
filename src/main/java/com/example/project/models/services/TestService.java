package com.example.project.models.services;

import com.example.project.dto.page.PageDto;
import com.example.project.dto.test.AddTestDto;
import com.example.project.dto.test.TestAnswerDto;
import com.example.project.dto.test.TestQuestionDto;
import com.example.project.dto.test.TestWalkthroughDto;
import com.example.project.models.entities.Test;
import com.example.project.models.entities.Topic;
import com.example.project.models.entities.User;

import java.util.List;
import java.util.Set;

public interface TestService {
    void add(User user, AddTestDto dto);

    void saveAnswer(User user, String testId, TestWalkthroughDto testWalkthroughDto);

    void start(User user, String testId);

    void finish(User user, String testId);

    PageDto<Test> getByUser(String type, String username, int page, int limit);

    Test getById(String id);

    boolean canWalkthrough(User user, String testId);

    boolean hasTimeToComplete(User user, String testId);

    boolean hasStarted(User user, String testId);

    boolean hasFinished(User user, String testId);

    TestQuestionDto getTestQuestionByNumber(String testId, int number);

    TestAnswerDto getTestQuestionAnswerByUserAndNumber(User user, String testId, int number);

    Set<Topic> getTestTopics(Test test);

    List<String> getTestDifficulties();
}
