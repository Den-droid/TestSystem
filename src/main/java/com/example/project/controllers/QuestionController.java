package com.example.project.controllers;

import com.example.project.models.services.QuestionService;
import org.springframework.stereotype.Controller;

@Controller
public class QuestionController {
    private QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }
}
