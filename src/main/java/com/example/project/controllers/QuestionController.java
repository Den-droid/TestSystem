package com.example.project.controllers;

import com.example.project.dto.question.AddQuestionDto;
import com.example.project.dto.question.EditQuestionDto;
import com.example.project.models.entities.Question;
import com.example.project.models.entities.User;
import com.example.project.models.enums.AnswerType;
import com.example.project.models.services.QuestionService;
import com.example.project.models.services.TopicService;
import com.example.project.models.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

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
                              @RequestParam(name = "media", required = false) MultipartFile file,
                              @ModelAttribute(name = "addQuestion") AddQuestionDto addQuestionDto) throws IOException {
        try {
            questionService.add(id, addQuestionDto, file);
        } catch (IllegalArgumentException ex) {
            return "redirect:/error";
        }

        String url = getRedirectUrlToUserQuestionPage(id);
        return "redirect:" + url;
    }

    @GetMapping("/topic/{id}/questions/edit/{questionId}")
    public String getEditPage(@PathVariable int id,
                              @PathVariable long questionId,
                              Model model) {
        if (!topicService.existsById(id) || !questionService.existsById(questionId))
            return "redirect:/error";

        Question question = questionService.getById(questionId);

        if (questionService.canBeChanged(question)) {
            model.addAttribute("question", question);
            model.addAttribute("questionTypes", questionService.getQuestionTypes());
            model.addAttribute("questionDifficulties", questionService.getQuestionDifficulties());
            model.addAttribute("answerTypes", questionService.getAnswerTypes());
            if (question.getAnswerType() == AnswerType.MATCH) {
                model.addAttribute("subQuestions", question.getSubQuestions());
                model.addAttribute("answers", questionService.getSubQuestionAnswers(question));
            } else {
                model.addAttribute("answers", question.getAnswers());
            }
        } else {
            String url = getRedirectUrlToUserQuestionPage(id) + "?error=edit";
            return "redirect:" + url;
        }

        return "questions/edit";
    }

    @PostMapping("/topic/{id}/questions/edit/{questionId}")
    public String editQuestion(@PathVariable int id,
                               @PathVariable long questionId,
                               @RequestParam(name = "media", required = false) MultipartFile file,
                               @ModelAttribute(name = "editQuestion") EditQuestionDto dto) throws IOException {
        questionService.edit(questionId, dto, file);

        String url = getRedirectUrlToUserQuestionPage(id);
        return "redirect:" + url;
    }

    @GetMapping("/topic/{id}/questions/delete/{questionId}")
    public String deleteQuestion(@PathVariable int id,
                                 @PathVariable long questionId) throws IOException {
        if (!topicService.existsById(id) || !questionService.existsById(questionId))
            return "redirect:/error";

        String url = getRedirectUrlToUserQuestionPage(id);

        try {
            questionService.delete(questionId);
        } catch (IllegalArgumentException ex) {
            url += "?error=delete";
        }

        return "redirect:" + url;
    }

    private String getRedirectUrlToUserQuestionPage(int topicId) {
        User user = userService.getCurrentLoggedIn();
        switch (user.getRole()) {
            case USER:
                return "/user/" + user.getUsername() + "/topic/" + topicId + "/questions";
            case ADMIN:
                return "/admin/topic/" + topicId + "/questions";
        }
        return null;
    }
}
