package com.example.project.models.services.impl;

import com.example.project.dto.question.AddQuestionDto;
import com.example.project.models.entities.Answer;
import com.example.project.models.entities.Question;
import com.example.project.models.entities.Topic;
import com.example.project.models.enums.AnswerType;
import com.example.project.models.enums.QuestionDifficulty;
import com.example.project.models.enums.QuestionType;
import com.example.project.models.mappers.AddQuestionMapper;
import com.example.project.models.repositories.AnswerRepository;
import com.example.project.models.repositories.QuestionRepository;
import com.example.project.models.repositories.TopicRepository;
import com.example.project.models.services.FileService;
import com.example.project.models.services.QuestionService;
import com.example.project.models.services.UserService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class QuestionServiceImpl implements QuestionService {
    private QuestionRepository questionRepository;
    private TopicRepository topicRepository;
    private AnswerRepository answerRepository;

    private UserService userService;

    private FileService fileService;

    public QuestionServiceImpl(QuestionRepository questionRepository,
                               FileService fileService,
                               UserService userService,
                               TopicRepository topicRepository,
                               AnswerRepository answerRepository) {
        this.questionRepository = questionRepository;
        this.fileService = fileService;
        this.userService = userService;
        this.topicRepository = topicRepository;
        this.answerRepository = answerRepository;
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
    public void edit() {

    }

    @Override
    public void delete(long id) {

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
