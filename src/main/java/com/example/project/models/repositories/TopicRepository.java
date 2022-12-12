package com.example.project.models.repositories;

import com.example.project.models.entities.Topic;
import com.example.project.models.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Collection;
import java.util.List;

public interface TopicRepository extends PagingAndSortingRepository<Topic, Integer> {
    Page<Topic> findAllByNameContainsIgnoreCase(String name, Pageable pageable);

    List<Topic> findAllByNameContainsIgnoreCase(String name);

    Page<Topic> findAllByNameContainsIgnoreCaseAndUser(String name, User user, Pageable pageable);

    boolean existsTopicByName(String name);

    Page<Topic> findAllByUser(User user, Pageable pageable);

    Topic findByName(String name);

    List<Topic> findAllByNameContainsIgnoreCaseAndNameNotIn(String name, Collection<String> namesNotIn);
}
