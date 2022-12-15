package com.example.project.models.repositories;

import com.example.project.models.entities.Question;
import com.example.project.models.entities.QuestionStatistic;
import org.springframework.data.repository.CrudRepository;

public interface QuestionStatisticRepository extends CrudRepository<QuestionStatistic, Long> {
    QuestionStatistic findByQuestion(Question question);
}