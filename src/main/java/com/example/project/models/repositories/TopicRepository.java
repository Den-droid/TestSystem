package com.example.project.models.repositories;

import com.example.project.models.entities.Topic;
import com.example.project.models.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TopicRepository extends PagingAndSortingRepository<Topic, Integer> {
    Page<Topic> findAllByNameContainsIgnoreCase(String name, Pageable pageable);

    Page<Topic> findAllByNameContainsIgnoreCaseAndUser(String name, User user, Pageable pageable);

    boolean existsTopicByName(String name);

    Page<Topic> findAllByUser(User user, Pageable pageable);
}
