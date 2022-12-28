package com.example.project.models.services;

import com.example.project.dto.page.PageDto;
import com.example.project.models.entities.Question;
import com.example.project.models.entities.Topic;
import com.example.project.models.entities.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TopicService {
    void add(User user, String topicName);

    void edit(int topicId, String topicName);

    void remove(int id, String transferToTopicName);

    Topic getById(int id);

    Topic getByName(String name);

    Integer getIdByQuestionId(Long id);

    List<Topic> getByNameContains(String part);

    PageDto<Topic> getPage(int page, int limit);

    PageDto<Topic> getPageByUser(int page, int limit, User user);

    PageDto<Topic> getPageByName(int page, int limit, String name);

    PageDto<Topic> getPageByNameAndUser(int page, int limit, User user, String name);

    boolean existsById(int id);

    List<Topic> getByNameContainsAndNamesNot(String namePart, List<String> namesNotIn);

    List<String> getTopicNamesByQuestions(List<Question> questions);
}
