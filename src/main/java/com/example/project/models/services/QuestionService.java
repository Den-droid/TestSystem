package com.example.project.models.services;

import com.example.project.dto.page.PageDto;
import com.example.project.dto.question.AddQuestionDto;
import com.example.project.dto.question.EditQuestionDto;
import com.example.project.models.entities.Answer;
import com.example.project.models.entities.Question;
import com.example.project.models.entities.QuestionStatistic;
import com.example.project.models.entities.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface QuestionService {
    void add(int topicId, User user, AddQuestionDto dto, MultipartFile file) throws IOException;

    void edit(long questionId, EditQuestionDto dto, MultipartFile file) throws IOException;

    void delete(long id) throws IOException;

    Question getById(long id);

    boolean canBeChanged(Question question);

    List<QuestionStatistic> getStatistic(List<Question> questions);

    void setStatistic(String testId, User user);

    void changeCoefficient(String testId);

    PageDto<Question> getPageByTopic(int topicId, int page, int limit);

    PageDto<Question> getPageByTopicAndName(String text, int topicId, int page, int limit);

    PageDto<Question> getPageByUser(User user, int page, int limit);

    PageDto<Question> getPageByUserAndName(User user, String text, int page, int limit);

    List<Answer> getSubQuestionAnswers(Question question);

    List<String> getQuestionTypes();

    List<String> getQuestionDifficulties();

    List<String> getAnswerTypes();

    List<Answer> getAnswers(Question question);

    List<Question> getSubQuestions(Question question);
}
