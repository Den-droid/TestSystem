package com.example.project.models.services;

import com.example.project.dto.question.AddQuestionDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface QuestionService {
    void add(int topicId, AddQuestionDto dto, MultipartFile file) throws IOException;

    void edit();

    void delete(long id);

    List<String> getQuestionTypes();

    List<String> getQuestionDifficulties();

    List<String> getAnswerTypes();
}
