package com.example.project.models.repositories;

import com.example.project.models.entities.Test;
import org.springframework.data.repository.CrudRepository;

public interface TestRepository extends CrudRepository<Test, String> {
}
