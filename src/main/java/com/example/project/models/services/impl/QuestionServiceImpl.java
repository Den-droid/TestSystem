package com.example.project.models.services.impl;

import com.example.project.models.repositories.QuestionRepository;
import com.example.project.models.services.QuestionService;

public class QuestionServiceImpl implements QuestionService {
    private QuestionRepository questionRepository;

    public QuestionServiceImpl(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }
}
