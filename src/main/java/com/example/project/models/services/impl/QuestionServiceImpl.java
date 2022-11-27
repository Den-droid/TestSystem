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
import java.util.*;
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
        if (file != null && !file.isEmpty()) {
            String mediaName = fileService.put(file);
            question.setMediaUrl(mediaName);
        }

        question.setTopic(topic);
        question.setUser(userService.getCurrentLoggedIn());

        questionRepository.save(question);
    }

    @Override
    public void edit(long questionId, EditQuestionDto dto, MultipartFile file) throws IOException {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(NoSuchElementException::new);

        Question editQuestion = EditQuestionMapper.map(dto);

        if (question.getAnswerType() != editQuestion.getAnswerType()) {
            if (question.getAnswerType() == AnswerType.MATCH) {
                question.deleteAllSubQuestions();
            } else {
                question.deleteAllAnswers();
            }

            if (editQuestion.getAnswerType() == AnswerType.MATCH) {
                for (Question subQuestion : editQuestion.getSubQuestions()) {
                    question.addSubQuestion(subQuestion);
                }
            } else {
                for (Answer answer : editQuestion.getAnswers()) {
                    question.addAnswer(answer);
                }
            }
        } else {
            if (question.getAnswerType() == AnswerType.MATCH) {
                List<Long> notDeletedIds = editQuestion.getSubQuestions().stream()
                        .map(Question::getId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                List<Question> deletedSubQuestions = questionRepository.findByIdNotInAndSupQuestion(
                        notDeletedIds, question
                );

                for (Question subQuestion : deletedSubQuestions) {
                    question.deleteSubQuestion(subQuestion);
                }
            } else {
                List<Long> notDeletedIds = editQuestion.getAnswers().stream()
                        .map(Answer::getId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                List<Answer> deletedAnswers = answerRepository.findAnswersByIdNotInAndQuestion(
                        notDeletedIds, question
                );

                for (Answer answer : deletedAnswers) {
                    question.deleteAnswer(answer);
                }
            }

            if (editQuestion.getAnswerType() == AnswerType.MATCH) {
                List<Question> addedSubQuestions = editQuestion.getSubQuestions().stream()
                        .filter(x -> x.getId() == null)
                        .collect(Collectors.toList());

                List<Question> existedSubQuestions = editQuestion.getSubQuestions().stream()
                        .filter(x -> x.getId() != null)
                        .collect(Collectors.toList());

                for (Question existedSubQuestion : existedSubQuestions) {
                    question.setSubQuestion(existedSubQuestion);
                }

                for (Question subQuestion : addedSubQuestions) {
                    question.addSubQuestion(subQuestion);
                }
            } else {
                List<Answer> addedAnswers = editQuestion.getAnswers().stream()
                        .filter(x -> x.getId() == null)
                        .collect(Collectors.toList());

                List<Answer> existedAnswers = editQuestion.getAnswers().stream()
                        .filter(x -> x.getId() != null)
                        .collect(Collectors.toList());

                for (Answer existedAnswer : existedAnswers) {
                    question.setAnswer(existedAnswer);
                }

                for (Answer answer : addedAnswers) {
                    question.addAnswer(answer);
                }
            }
        }

        if (file != null && !file.isEmpty()) {
            if (question.getMediaUrl() != null)
                fileService.delete(question.getMediaUrl());
            String mediaName = fileService.put(file);
            question.setMediaUrl(mediaName);
        } else {
            if (editQuestion.getType() == QuestionType.TEXT &&
                    question.getType() != QuestionType.TEXT)
                fileService.delete(question.getMediaUrl());
        }

        question.setType(editQuestion.getType());
        question.setDifficulty(editQuestion.getDifficulty());
        question.setAnswerType(editQuestion.getAnswerType());
        question.setText(editQuestion.getText());
        question.setAnswerDescription(editQuestion.getAnswerDescription());
        questionRepository.save(question);
    }

    @Override
    public void delete(long id) throws IOException {
        Question question = questionRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);
        if (canBeChanged(question)) {
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
