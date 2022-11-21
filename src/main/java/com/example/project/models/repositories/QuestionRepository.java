package com.example.project.models.repositories;

import com.example.project.models.entities.Question;
import com.example.project.models.entities.Topic;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface QuestionRepository extends PagingAndSortingRepository<Question, Long> {
    List<Question> findByTopic(Topic topic);
}
