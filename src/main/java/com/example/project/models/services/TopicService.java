package com.example.project.models.services;

import com.example.project.models.entities.Topic;

import java.util.List;

public interface TopicService {
    void add(String username, String topicName);

    void edit(int topicId, String topicName);

    void remove(int id);

    Topic getById(int id);

    List<Topic> getPage(int page, int limit);

    List<Topic> getPageByUsername(int page, int limit, String username);

    List<Topic> searchByName(String name);

    List<Topic> searchByNameAndUserId(String userId, String name);
}
