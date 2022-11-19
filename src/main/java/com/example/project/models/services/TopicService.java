package com.example.project.models.services;

import com.example.project.models.entities.Topic;
import org.springframework.data.domain.Page;

public interface TopicService {
    void add(String username, String topicName);

    void edit(int topicId, String topicName);

    void remove(int id);

    Topic getById(int id);

    Page<Topic> getPage(int page, int limit);

    Page<Topic> getPageByUsername(int page, int limit, String username);

    Page<Topic> getSearchPageByName(int page, int limit, String name);

    Page<Topic> getSearchPageByNameAndUsername(int page, int limit, String username, String name);
}
