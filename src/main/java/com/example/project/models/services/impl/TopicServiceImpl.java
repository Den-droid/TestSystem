package com.example.project.models.services.impl;

import com.example.project.dto.page.PageDto;
import com.example.project.models.entities.Question;
import com.example.project.models.entities.Test;
import com.example.project.models.entities.Topic;
import com.example.project.models.entities.User;
import com.example.project.models.repositories.QuestionRepository;
import com.example.project.models.repositories.TestRepository;
import com.example.project.models.repositories.TopicRepository;
import com.example.project.models.services.TopicService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
public class TopicServiceImpl implements TopicService {
    private final TopicRepository topicRepository;
    private final QuestionRepository questionRepository;
    private final TestRepository testRepository;

    public TopicServiceImpl(TopicRepository repository,
                            QuestionRepository questionRepository,
                            TestRepository testRepository) {
        this.topicRepository = repository;
        this.questionRepository = questionRepository;
        this.testRepository = testRepository;
    }

    @Override
    public void add(User user, String topicName) {
        if (topicRepository.existsTopicByName(topicName)) {
            throw new IllegalArgumentException("There is already topic with such name!!!");
        }
        Topic toSave = new Topic();
        toSave.setName(topicName);
        toSave.setUser(user);
        topicRepository.save(toSave);
    }

    @Override
    public void edit(int topicId, String topicName) {
        if (topicRepository.existsTopicByName(topicName)) {
            throw new IllegalArgumentException("There is already topic with such name!!!");
        }

        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(NoSuchElementException::new);
        topic.setName(topicName);
        topicRepository.save(topic);
    }

    @Override
    public void remove(int id, String transferToTopicName) {
        Topic topicToTransfer = topicRepository.findByName(transferToTopicName);
        Topic topicToDelete = topicRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);

        List<Question> questions = questionRepository.findByTopic(topicToDelete);
        questions.forEach(x -> x.setTopic(topicToTransfer));
        questionRepository.saveAll(questions);

        List<Test> tests = testRepository.findTestsByTopicsId(topicToDelete.getId());
        tests.forEach(x -> {
            x.getTopics().remove(topicToDelete);
            x.getTopics().add(topicToTransfer);
        });
        testRepository.saveAll(tests);

        topicRepository.delete(topicToDelete);
    }

    @Override
    public Topic getById(int id) {
        return topicRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);
    }

    @Override
    public Topic getByName(String name) {
        return topicRepository.findByName(name);
    }

    @Override
    public Integer getIdByQuestionId(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);
        return question.getTopic().getId();
    }

    @Override
    public List<Topic> getByNameContains(String part) {
        return topicRepository.findAllByNameContainsIgnoreCase(part);
    }

    @Override
    public PageDto<Topic> getPage(int page, int limit) {
        Page<Topic> topics = topicRepository.findAll(PageRequest.of(page - 1, limit));
        return new PageDto<>(topics.getContent(), page, topics.getTotalPages());
    }

    @Override
    public PageDto<Topic> getPageByUser(int page, int limit, User user) {
        Page<Topic> topics = topicRepository.findAllByUser(user,
                PageRequest.of(page - 1, limit));
        return new PageDto<>(topics.getContent(), page, topics.getTotalPages());
    }

    @Override
    public PageDto<Topic> getPageByName(int page, int limit, String name) {
        Page<Topic> topics = topicRepository.findAllByNameContainsIgnoreCase(name,
                PageRequest.of(page - 1, limit));
        return new PageDto<>(topics.getContent(), page, topics.getTotalPages());
    }

    @Override
    public PageDto<Topic> getPageByNameAndUser(int page, int limit, User user, String name) {
        Page<Topic> topics = topicRepository
                .findAllByNameContainsIgnoreCaseAndUser(name, user,
                        PageRequest.of(page - 1, limit));
        return new PageDto<>(topics.getContent(), page, topics.getTotalPages());
    }

    @Override
    public boolean existsById(int id) {
        return topicRepository.existsById(id);
    }

    @Override
    public List<Topic> getByNameContainsAndNamesNot(String namePart, List<String> namesNotIn) {
        if (namesNotIn == null || namesNotIn.size() == 0) {
            return topicRepository.findAllByNameContainsIgnoreCase(namePart);
        }
        return topicRepository
                .findAllByNameContainsIgnoreCaseAndNameNotIn(namePart, namesNotIn);
    }

    @Override
    public List<String> getTopicNamesByQuestions(List<Question> questions) {
        return questions.stream()
                .map(x -> x.getTopic().getName())
                .collect(Collectors.toList());
    }
}