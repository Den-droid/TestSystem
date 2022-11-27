package com.example.project.models.services.impl;

import com.example.project.models.entities.Question;
import com.example.project.models.entities.Test;
import com.example.project.models.entities.Topic;
import com.example.project.models.entities.User;
import com.example.project.models.repositories.QuestionRepository;
import com.example.project.models.repositories.TestRepository;
import com.example.project.models.repositories.TopicRepository;
import com.example.project.models.repositories.UserRepository;
import com.example.project.models.services.TopicService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
public class TopicServiceImpl implements TopicService {
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;

    private final QuestionRepository questionRepository;

    private final TestRepository testRepository;

    public TopicServiceImpl(TopicRepository repository,
                            UserRepository userRepository,
                            QuestionRepository questionRepository,
                            TestRepository testRepository) {
        this.userRepository = userRepository;
        this.topicRepository = repository;
        this.questionRepository = questionRepository;
        this.testRepository = testRepository;
    }

    @Override
    public void add(String username, String topicName) {
        User user = userRepository.findByUsernameIgnoreCase(username);
        if (user == null)
            throw new UsernameNotFoundException("There is no user with such username!!!");
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
                .orElseThrow(NoSuchElementException::new);
        if (topicRepository.existsTopicByName(topicName)) {
            throw new IllegalArgumentException("There is already topic with such name!!!");
        }
        topic.setName(topicName);
        topicRepository.save(topic);
    }

    @Override
    public void remove(int id, String transferToTopicName) {
        Topic topicToTransfer = topicRepository.findByName(transferToTopicName);
        Topic topicToDelete = topicRepository.findById(id).orElseThrow(IllegalArgumentException::new);

        List<Question> questions = questionRepository.findByTopic(topicToDelete);
        questions.forEach(x -> x.setTopic(topicToTransfer));
        questionRepository.saveAll(questions);

//        List<Test> tests = testRepository.findByTopics(Collections.singletonList(topicToDelete));
//        tests.forEach(x -> x.setTopics(Collections.singleton(topicToTransfer)));

        topicRepository.delete(topicToDelete);
    }

    @Override
    public Topic getById(int id) {
        return topicRepository.findById(id)
                .orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public Page<Topic> getPage(int pageNumber, int limit) {
        return topicRepository.findAll(PageRequest.of(pageNumber - 1, limit));
    }

    @Override
    public Page<Topic> getPageByUsername(int page, int limit, String username) {
        User user = userRepository.findByUsernameIgnoreCase(username);
        if (user == null)
            throw new IllegalArgumentException();
        return topicRepository.findAllByUser(user, PageRequest.of(page - 1, limit));
    }

    @Override
    public Page<Topic> getSearchPageByName(int page, int limit, String name) {
        return topicRepository.findAllByNameContainsIgnoreCase(name, PageRequest.of(page - 1, limit));
    }

    @Override
    public Page<Topic> getSearchPageByNameAndUsername(int page, int limit, String username, String name) {
        User user = userRepository.findByUsernameIgnoreCase(username);
        if (user == null)
            throw new IllegalArgumentException();
        return topicRepository.findAllByNameContainsIgnoreCaseAndUser(name, user,
                PageRequest.of(page - 1, limit));
    }

    @Override
    public boolean existsById(int id) {
        return topicRepository.existsById(id);
    }
}
