package com.example.project.controllers;

import com.example.project.dto.page.PageDto;
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
import java.util.NoSuchElementException;

@Controller
public class QuestionController {
    private final QuestionService questionService;
    private final TopicService topicService;
    private final UserService userService;

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
    public String add(@PathVariable int id,
                      @RequestParam(name = "media", required = false) MultipartFile file,
                      @ModelAttribute(name = "addQuestion") AddQuestionDto addQuestionDto)
            throws IOException {
        try {
            questionService.add(id, userService.getCurrentLoggedIn(),
                    addQuestionDto, file);
        } catch (NoSuchElementException ex) {
            return "redirect:/error";
        }

        String url = getRedirectUrlToQuestionPage(null);
        return "redirect:" + url;
    }

    @GetMapping("/questions/edit/{questionId}")
    public String getEditPage(@PathVariable long questionId,
                              Model model) {
        if (!questionService.existsById(questionId))
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
            String url = getRedirectUrlToQuestionPage(null) + "?error=edit";
            return "redirect:" + url;
        }

        return "questions/edit";
    }

    @PostMapping("/questions/edit/{questionId}")
    public String edit(@PathVariable long questionId,
                       @RequestParam(name = "media", required = false) MultipartFile file,
                       @ModelAttribute(name = "editQuestion") EditQuestionDto dto)
            throws IOException {
        questionService.edit(questionId, dto, file);

        String url = getRedirectUrlToQuestionPage(null);
        return "redirect:" + url;
    }

    @GetMapping("/questions/delete/{questionId}")
    public String delete(@PathVariable long questionId) throws IOException {
        if (!questionService.existsById(questionId))
            return "redirect:/error";

        String url = getRedirectUrlToQuestionPage(questionService.
                getById(questionId).getTopic().getId());

        try {
            questionService.delete(questionId);
        } catch (IllegalArgumentException ex) {
            url += "?error=delete";
        }

        return "redirect:" + url;
    }

    @GetMapping("/admin/topic/{id}/questions")
    public String redirectToAdminPage(@PathVariable int id) {
        String url = "/admin/topic/" + id + "/questions/" + 1;
        return "redirect:" + url;
    }

    @GetMapping("/topic/{id}/questions")
    public String redirectToPage(@PathVariable int id) {
        String url = "/topic/" + id + "/questions/" + 1;
        return "redirect:" + url;
    }

    @GetMapping("/user/questions")
    public String redirectToUserPage() {
        String url = "/user/questions/" + 1;
        return "redirect:" + url;
    }

    @GetMapping("/admin/topic/{id}/questions/{page}")
    public String getByPageAndTopicForAdmin(@PathVariable int id,
                                            @PathVariable int page,
                                            @RequestParam(name = "text", required = false) String text,
                                            @RequestParam(name = "error", required = false) String error,
                                            Model model) {
        if (page < 1)
            return "redirect:/error";

        try {
            PageDto<Question> questions;
            if (text != null) {
                questions = questionService.getPageByTopicAndName(text, id, 1, 10);
            } else {
                questions = questionService.getPageByTopic(id, page, 10);
            }
            model.addAttribute("questions", questions.getElements());
            model.addAttribute("currentPage", questions.getCurrentPage());
            model.addAttribute("totalPages", questions.getTotalPages());
        } catch (NoSuchElementException ex) {
            return "redirect:/error";
        }

        if (error != null && error.equals("delete"))
            model.addAttribute("error", "You can't delete this question because " +
                    "it's used somewhere else");

        return "admin/questions";
    }

    @GetMapping("/topic/{id}/questions/{page}")
    public String getByPageAndTopic(@PathVariable int page,
                                    @PathVariable int id,
                                    @RequestParam(name = "text", required = false) String text,
                                    Model model) {
        if (page < 1)
            return "redirect:/error";

        try {
            PageDto<Question> questions;
            if (text != null) {
                questions = questionService.getPageByTopicAndName(text, id, 1, 10);
            } else {
                questions = questionService.getPageByTopic(id, page, 10);
            }
            model.addAttribute("questions", questions.getElements());
            model.addAttribute("currentPage", questions.getCurrentPage());
            model.addAttribute("totalPages", questions.getTotalPages());
            model.addAttribute("statistics", questionService.getStatistic(questions.getElements()));
        } catch (NoSuchElementException ex) {
            return "redirect:/error";
        }

        return "main/questions";
    }

    @GetMapping("/user/questions/{page}")
    public String getByPageAndUsername(@PathVariable int page,
                                       @RequestParam(name = "text", required = false) String text,
                                       @RequestParam(name = "error", required = false) String error,
                                       Model model) {
        if (page < 1)
            return "redirect:/error";

        PageDto<Question> questions;
        if (text != null) {
            questions = questionService.getPageByUserAndName(
                    userService.getCurrentLoggedIn(), text, 1, 10);
        } else {
            questions = questionService.getPageByUser(
                    userService.getCurrentLoggedIn(), page, 10);
        }
        model.addAttribute("questions", questions.getElements());
        model.addAttribute("currentPage", questions.getCurrentPage());
        model.addAttribute("totalPages", questions.getTotalPages());
        model.addAttribute("statistics", questionService.getStatistic(questions.getElements()));

        if (error != null) {
            if (error.equals("edit"))
                model.addAttribute("error", "You can't edit this question because " +
                        "it's used somewhere else");
            if (error.equals("delete"))
                model.addAttribute("error", "You can't delete this question because " +
                        "it's used somewhere else");
        }

        return "user/questions";
    }

    private String getRedirectUrlToQuestionPage(Integer topicId) {
        User user = userService.getCurrentLoggedIn();
        switch (user.getRole()) {
            case USER:
                return "/user/questions/1";
            case ADMIN:
                return "/admin/topic/" + topicId + "/questions/1";
        }
        return null;
    }
}
