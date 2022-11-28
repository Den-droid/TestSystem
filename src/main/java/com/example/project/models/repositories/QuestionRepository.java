package com.example.project.models.repositories;

import com.example.project.models.entities.Question;
import com.example.project.models.entities.Topic;
import com.example.project.models.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface QuestionRepository extends PagingAndSortingRepository<Question, Long> {
    List<Question> findByTopic(Topic topic);

    List<Question> findByIdNotInAndSupQuestion(List<Long> ids, Question supQuestion);

    Page<Question> findAllByTopic(Topic topic, Pageable pageable);

    Page<Question> findAllByTopicAndTextContainsIgnoreCase(Topic topic, String text, Pageable pageable);

    Page<Question> findAllByUser(User user, Pageable pageable);

    Page<Question> findAllByUserAndTextContainsIgnoreCase(User user, String text, Pageable pageable);
}
