package com.example.project.models.repositories;

import com.example.project.models.entities.Test;
import com.example.project.models.entities.Topic;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface TestRepository extends CrudRepository<Test, String> {
    List<Test> findByTopics(List<Topic> topics);
}
