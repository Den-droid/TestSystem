package com.example.project.controllers;

import com.example.project.dto.question.AddQuestionDto;
import com.example.project.models.entities.User;
import com.example.project.models.services.QuestionService;
import com.example.project.models.services.TopicService;
import com.example.project.models.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Controller
public class QuestionController {
    private QuestionService questionService;
    private TopicService topicService;

    private UserService userService;

    public QuestionController(QuestionService questionService,
                              TopicService topicService,
                              UserService userService) {
        this.questionService = questionService;
        this.topicService = topicService;
        this.userService = userService;
    }

    @GetMapping("/topic/{id}/questions/add")
    public String getAddPage(@PathVariable int id, Model model) {
        if (!topicService.existsById(id))
            return "redirect:/error";
        model.addAttribute("topicId", id);
        model.addAttribute("questionTypes", questionService.getQuestionTypes());
        model.addAttribute("questionDifficulties", questionService.getQuestionDifficulties());
        model.addAttribute("answerTypes", questionService.getAnswerTypes());
        return "questions/add";
    }

    @PostMapping("/topic/{id}/questions/add")
    public String addQuestion(@PathVariable int id,
                              @RequestParam(name = "media") MultipartFile file,
                              @ModelAttribute(name = "addQuestion") AddQuestionDto addQuestionDto) throws IOException {
        try {
            questionService.add(id, addQuestionDto, file);
        } catch (IllegalArgumentException ex) {
            return "redirect:/error";
        }

        String url = getRedirectUrlToUserTopicPage(id);
        return "redirect:" + url;
    }

    private String getRedirectUrlToUserTopicPage(int topicId) {
        User user = userService.getCurrentLoggedIn();
        return "/user/" + user.getUsername() + "/topic/" + topicId + "/questions";
    }
}
