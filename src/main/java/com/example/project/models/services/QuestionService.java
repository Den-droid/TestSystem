package com.example.project.models.services;

import com.example.project.dto.question.AddQuestionDto;
import com.example.project.dto.question.EditQuestionDto;
import com.example.project.models.entities.Answer;
import com.example.project.models.entities.Question;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface QuestionService {
    void add(int topicId, AddQuestionDto dto, MultipartFile file) throws IOException;

    void edit(long questionId, EditQuestionDto dto, MultipartFile file) throws IOException;

    void delete(long id) throws IOException;

    boolean existsById(long id);

    Question getById(long id);

    boolean canBeChanged(Question question);

    Page<Question> getPageByTopic(int topicId, int page, int limit);

    Page<Question> getPageByTopicAndName(String text, int topicId, int page, int limit);

    Page<Question> getPageByUser(String username, int page, int limit);

    Page<Question> getPageByUserAndName(String username, String text, int page, int limit);

    List<Answer> getSubQuestionAnswers(Question question);

    List<String> getQuestionTypes();

    List<String> getQuestionDifficulties();

    List<String> getAnswerTypes();
}
