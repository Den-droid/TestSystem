package com.example.project.models.repositories;

import com.example.project.models.entities.Question;
import com.example.project.models.entities.Test;
import com.example.project.models.entities.TestAnswer;
import com.example.project.models.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TestAnswerRepository extends CrudRepository<TestAnswer, Long> {
    List<TestAnswer> findByTestAndUserAndQuestion(Test test, User user, Question question);

    List<TestAnswer> findByTestAndUserAndQuestionAndAnswerNotIn(Test test, User user,
                                                                Question question, List<String> answer);

    List<TestAnswer> findByTestAndUserAndQuestionAndAnswerIn(Test test, User user,
                                                             Question question, List<String> answer);

    List<TestAnswer> findByTestAndUser(Test test, User user);

}
