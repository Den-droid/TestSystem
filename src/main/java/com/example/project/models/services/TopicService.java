package com.example.project.models.services;

import com.example.project.models.entities.Topic;
import org.springframework.data.domain.Page;

import java.util.List;

public interface TopicService {
    void add(String username, String topicName);

    void edit(int topicId, String topicName);

    void remove(int id, String transferToTopicName);

    Topic getById(int id);

    List<Topic> getByNameContains(String part);

    Page<Topic> getPage(int page, int limit);

    Page<Topic> getPageByUsername(int page, int limit, String username);

    Page<Topic> getPageByName(int page, int limit, String name);

    Page<Topic> getPageByNameAndUsername(int page, int limit, String username, String name);

    boolean existsById(int id);
}
