package com.example.project.models.repositories;

import com.example.project.models.entities.Topic;
import com.example.project.models.entities.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface TopicRepository extends PagingAndSortingRepository<Topic, Integer> {
    List<Topic> findAllByNameContainsIgnoreCase(String name, Pageable pageable);

    List<Topic> findAllByNameContainsIgnoreCaseAndUser(String name, User user, Pageable pageable);

    boolean existsTopicByName(String name);

    List<Topic> findAllByUser(User user, Pageable pageable);
}
