package com.example.project.models.services;

import com.example.project.dto.page.PageDto;
import com.example.project.models.entities.Topic;
import com.example.project.models.entities.User;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TopicService {
    void add(User user, String topicName);

    void edit(int topicId, String topicName);

    void remove(int id, String transferToTopicName);

    Topic getById(int id);

    List<Topic> getByNameContains(String part);

    PageDto<Topic> getPage(int page, int limit);

    PageDto<Topic> getPageByUsername(int page, int limit, String username);

    PageDto<Topic> getPageByName(int page, int limit, String name);

    PageDto<Topic> getPageByNameAndUsername(int page, int limit, String username, String name);

    boolean existsById(int id);

    List<Topic> getByNameContainsAndNamesNot(String namePart, List<String> namesNotIn);
}
