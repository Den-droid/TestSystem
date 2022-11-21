package com.example.project.controllers;

import com.example.project.models.services.QuestionService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class QuestionController {
    private QuestionService questionService;

    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @GetMapping("/questions/add")
    public String getAddPage() {
        return "questions/add";
    }

    @PostMapping("/questions/add")
    public String addQuestion() {
        return "";
    }
}
