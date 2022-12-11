package com.example.project.models.services;

import com.example.project.dto.page.PageDto;
import com.example.project.dto.test.AddTestDto;
import com.example.project.dto.test.TestWalkthroughDto;
import com.example.project.models.entities.Question;
import com.example.project.models.entities.Test;
import com.example.project.models.entities.Topic;
import com.example.project.models.entities.User;

import java.util.List;
import java.util.Set;

public interface TestService {
    void add(User user, AddTestDto dto);

    void start(User user, String testId);

    void finish(User user, String testId, TestWalkthroughDto dto);

    PageDto<Test> getByUser(String type, String username, int page, int limit);

    Test getById(String id);

    boolean canWalkthrough(User user, String testId);

    List<Question> getTestQuestions(Test test);

    Set<Topic> getTestTopics(Test test);

    List<String> getTestDifficulties();
}
