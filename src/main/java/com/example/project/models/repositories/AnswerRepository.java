package com.example.project.models.repositories;

import com.example.project.models.entities.Answer;
import com.example.project.models.entities.Question;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AnswerRepository extends CrudRepository<Answer, Long> {
    List<Answer> findAnswersByIdNotInAndQuestion(List<Long> ids,
                                                 Question question);
}
