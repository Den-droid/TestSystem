package com.example.project.models.repositories;

import com.example.project.models.entities.Question;
import com.example.project.models.entities.Test;
import com.example.project.models.entities.TestQuestion;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TestQuestionRepository extends CrudRepository<TestQuestion, Long> {
    List<TestQuestion> findByQuestion(Question question);

    List<TestQuestion> findAllByTest(Test test);
}
