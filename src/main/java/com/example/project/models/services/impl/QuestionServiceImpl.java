package com.example.project.models.services.impl;

import com.example.project.dto.question.AddQuestionDto;
import com.example.project.dto.question.EditQuestionDto;
import com.example.project.models.entities.*;
import com.example.project.models.enums.AnswerType;
import com.example.project.models.enums.QuestionDifficulty;
import com.example.project.models.enums.QuestionType;
import com.example.project.models.mappers.AddQuestionMapper;
import com.example.project.models.mappers.EditQuestionMapper;
import com.example.project.models.repositories.AnswerRepository;
import com.example.project.models.repositories.QuestionRepository;
import com.example.project.models.repositories.TestQuestionRepository;
import com.example.project.models.repositories.TopicRepository;
import com.example.project.models.services.FileService;
import com.example.project.models.services.QuestionService;
import com.example.project.models.services.UserService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional
public class QuestionServiceImpl implements QuestionService {
    private QuestionRepository questionRepository;
    private TopicRepository topicRepository;
    private AnswerRepository answerRepository;
    private TestQuestionRepository testQuestionRepository;
    private UserService userService;

    private FileService fileService;

    public QuestionServiceImpl(QuestionRepository questionRepository,
                               FileService fileService,
                               UserService userService,
                               TopicRepository topicRepository,
                               AnswerRepository answerRepository,
                               TestQuestionRepository testQuestionRepository) {
        this.questionRepository = questionRepository;
        this.fileService = fileService;
        this.userService = userService;
        this.topicRepository = topicRepository;
        this.answerRepository = answerRepository;
        this.testQuestionRepository = testQuestionRepository;
    }

    @Override
    public void add(int topicId, AddQuestionDto dto, MultipartFile file) throws IOException {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(IllegalArgumentException::new);

        Question question = AddQuestionMapper.map(dto);
        if (file != null) {
            String mediaName = fileService.put(file);
            question.setMediaUrl(mediaName);
        }

        question.setTopic(topic);
        question.setUser(userService.getCurrentLoggedIn());

        questionRepository.save(question);

        if (question.getAnswerType() == AnswerType.MATCH) {
            questionRepository.saveAll(question.getSubQuestions());
            for (Question subQuestion : question.getSubQuestions()) {
                answerRepository.saveAll(subQuestion.getAnswers());
            }
        } else {
            List<Answer> answers = question.getAnswers();
            answerRepository.saveAll(answers);
        }
    }

    @Override
    public void edit(long questionId, EditQuestionDto dto, MultipartFile file) throws IOException {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(NoSuchElementException::new);

        Question editQuestion = EditQuestionMapper.map(dto);

        if (!file.isEmpty()) {
            if (question.getMediaUrl() != null)
                fileService.delete(question.getMediaUrl());
            String mediaName = fileService.put(file);
            question.setMediaUrl(mediaName);
        } else {
            if (editQuestion.getType() == QuestionType.TEXT &&
                    question.getType() != QuestionType.TEXT)
                fileService.delete(question.getMediaUrl());
        }

        // need to write
    }

    @Override
    public void delete(long id) throws IOException {
        Question question = questionRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);
        if (canBeChanged(question)) {
            if (question.getAnswerType() == AnswerType.MATCH) {
                for (Question subQuestion : question.getSubQuestions()) {
                    answerRepository.deleteAll(subQuestion.getAnswers());
                }
                questionRepository.deleteAll(question.getSubQuestions());
            } else {
                List<Answer> answers = question.getAnswers();
                answerRepository.deleteAll(answers);
            }

            if (question.getMediaUrl() != null)
                fileService.delete(question.getMediaUrl());

            questionRepository.delete(question);
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public boolean existsById(long id) {
        return questionRepository.existsById(id);
    }

    @Override
    public Question getById(long id) {
        return questionRepository.findById(id).orElseThrow(IllegalArgumentException::new);
    }

    @Override
    public List<Answer> getSubQuestionAnswers(Question question) {
        if (question.getSubQuestions() != null) {
            return question.getSubQuestions().stream()
                    .map(Question::getAnswers)
                    .collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);
        } else
            return null;
    }

    @Override
    public boolean canBeChanged(Question question) {
        List<TestQuestion> tests = testQuestionRepository.findByQuestion(question);
        return tests == null || tests.size() == 0;
    }

    @Override
    public List<String> getQuestionTypes() {
        return Arrays.stream(QuestionType.values())
                .map(QuestionType::getText)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getQuestionDifficulties() {
        return Arrays.stream(QuestionDifficulty.values())
                .map(QuestionDifficulty::getText)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getAnswerTypes() {
        return Arrays.stream(AnswerType.values())
                .map(AnswerType::getText)
                .collect(Collectors.toList());
    }
}
