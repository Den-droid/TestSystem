package com.example.project.models.services.impl;

import com.example.project.dto.topic.TopicPageDto;
import com.example.project.models.entities.Topic;
import com.example.project.models.entities.User;
import com.example.project.models.repositories.TopicRepository;
import com.example.project.models.repositories.UserRepository;
import com.example.project.models.services.TopicService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class TopicServiceImpl implements TopicService {
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;

    public TopicServiceImpl(TopicRepository repository, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.topicRepository = repository;
    }

    @Override
    public void add(String username, String topicName) {
        User user = userRepository.findByUsernameIgnoreCase(username);
        if (user == null)
            throw new IllegalArgumentException("There is no user with such username!!!");
        if (topicRepository.existsTopicByName(topicName))
            throw new IllegalArgumentException("There is already topic with such name!!!");
        Topic toSave = new Topic();
        toSave.setName(topicName);
        toSave.setUser(user);
        topicRepository.save(toSave);
    }

    @Override
    public void edit(int topicId, String topicName) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(() -> new IllegalArgumentException("There is no topic with such id!!!"));
        topic.setName(topicName);
        topicRepository.save(topic);
    }
    @Override
    public void remove(int id) {
        topicRepository.deleteById(id);
    }
    @Override
    public Topic getById(int id) {
        return topicRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("There is no topic with such id!!!"));
    }
    @Override
    public List<Topic> getPage(int pageNumber, int limit) {
        Page<Topic> page = topicRepository.findAll(PageRequest.of(pageNumber, limit));
        return page.getContent();
    }
    @Override
    public List<Topic> getPageByUsername(int page, int limit, String username) {
        User user = userRepository.findByUsernameIgnoreCase(username);
        if (user == null)
            throw new IllegalArgumentException();
        return topicRepository.findAllByUser(user, PageRequest.of(page, limit));
    }

    @Override
    public List<Topic> searchByName(String name) {
        return null;
    }

    @Override
    public List<Topic> searchByNameAndUserId(String userId, String name) {
        return null;
    }
}
