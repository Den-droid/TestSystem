package com.example.project.controllers;

import com.example.project.dto.page.PageDto;
import com.example.project.dto.question.AddQuestionDto;
import com.example.project.dto.question.AddQuestionPageDto;
import com.example.project.dto.question.EditQuestionDto;
import com.example.project.dto.question.EditQuestionPageDto;
import com.example.project.models.entities.Question;
import com.example.project.models.entities.User;
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
        if (!topicService.existsById(id)) {
            return "redirect:/error";
        }
        model.addAttribute("topicId", id);

        AddQuestionPageDto dto = questionService.getAddQuestionPage();
        model.addAttribute("questionTypes", dto.getQuestionTypes());
        model.addAttribute("questionDifficulties", dto.getQuestionDifficulties());
        model.addAttribute("answerTypes", dto.getAnswerTypes());
        return "questions/add";
    }

    @PostMapping("/topic/{id}/questions/add")
    public String add(@PathVariable int id,
                      @RequestParam(name = "media", required = false) MultipartFile file,
                      @ModelAttribute(name = "addQuestion") AddQuestionDto addQuestionDto)
            throws IOException {
        User user = userService.getCurrentLoggedIn();

        try {
            questionService.add(id, user, addQuestionDto, file);
        } catch (NoSuchElementException ex) {
            return "redirect:/error";
        }

        String url = getRedirectUrlToQuestionPage(user, null);
        return "redirect:" + url;
    }

    @GetMapping("/questions/edit/{questionId}")
    public String getEditPage(@PathVariable long questionId,
                              Model model) {
        try {
            Question question = questionService.getById(questionId);

            if (questionService.canBeChanged(question)) {
                EditQuestionPageDto dto = questionService.getEditQuestionPage(question);
                model.addAttribute("question", question);
                model.addAttribute("questionTypes", dto.getQuestionTypes());
                model.addAttribute("questionDifficulties", dto.getQuestionDifficulties());
                model.addAttribute("answerTypes", dto.getAnswerTypes());
                model.addAttribute("subQuestions", dto.getSubQuestions());
                model.addAttribute("answers", dto.getSubQuestions());
            } else {
                User user = userService.getCurrentLoggedIn();
                String url = getRedirectUrlToQuestionPage(user, null) + "?error=edit";
                return "redirect:" + url;
            }
        } catch (NoSuchElementException ex) {
            return "redirect:/error";
        }

        return "questions/edit";
    }

    @PostMapping("/questions/edit/{questionId}")
    public String edit(@PathVariable long questionId,
                       @RequestParam(name = "media", required = false) MultipartFile file,
                       @ModelAttribute(name = "editQuestion") EditQuestionDto dto)
            throws IOException {
        questionService.edit(questionId, dto, file);

        User user = userService.getCurrentLoggedIn();
        String url = getRedirectUrlToQuestionPage(user, null);
        return "redirect:" + url;
    }

    @GetMapping("/questions/delete/{questionId}")
    public String delete(@PathVariable long questionId) throws IOException {
        String url = "";

        try {
            questionService.delete(questionId);

            User user = userService.getCurrentLoggedIn();
            url = getRedirectUrlToQuestionPage(user, topicService.getIdByQuestionId(questionId));
        } catch (NoSuchElementException ex) {
            return "redirect:/error";
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
    public String getPageByTopicForAdmin(@PathVariable int id,
                                         @PathVariable int page,
                                         @RequestParam(name = "error",
                                                 required = false) String error,
                                         Model model) {
        if (page < 1) {
            return "redirect:/error";
        }

        try {
            PageDto<Question> questions = questionService.getPageByTopic(id, page, 10);
            model.addAttribute("questions", questions.getElements());
            model.addAttribute("currentPage", questions.getCurrentPage());
            model.addAttribute("totalPages", questions.getTotalPages());
            model.addAttribute("topicName", topicService.getById(id).getName());
        } catch (NoSuchElementException ex) {
            return "redirect:/error";
        }

        if (error != null && (error.equals("delete") || error.equals("edit"))) {
            model.addAttribute("error", "You can't do this action because "
                    + "this question because it's used somewhere else");
        }

        return "admin/questions";
    }

    @GetMapping("/admin/topic/{id}/questions/search")
    public String getPageByTopicAndNameForAdmin(@PathVariable int id,
                                                @RequestParam(name = "query") String text,
                                                @RequestParam(name = "page",
                                                        required = false) Integer page,
                                                Model model) {
        if (page == null) {
            page = 1;
        } else if (page < 1) {
            return "redirect:/error";
        }

        try {
            PageDto<Question> questions = questionService.getPageByTopicAndName(text, id, page, 10);
            model.addAttribute("questions", questions.getElements());
            model.addAttribute("currentPage", questions.getCurrentPage());
            model.addAttribute("totalPages", questions.getTotalPages());
            model.addAttribute("isSearch", true);
            model.addAttribute("text", text);
            model.addAttribute("topicName", topicService.getById(id).getName());
        } catch (NoSuchElementException ex) {
            return "redirect:/error";
        }

        return "admin/questions";
    }

    @GetMapping("/topic/{id}/questions/{page}")
    public String getPageByTopic(@PathVariable int page,
                                 @PathVariable int id,
                                 Model model) {
        if (page < 1) {
            return "redirect:/error";
        }

        try {
            PageDto<Question> questions = questionService.getPageByTopic(id, page, 10);

            model.addAttribute("questions", questions.getElements());
            model.addAttribute("currentPage", questions.getCurrentPage());
            model.addAttribute("totalPages", questions.getTotalPages());
            model.addAttribute("statistics", questionService.getStatistic(questions.getElements()));
            model.addAttribute("topicName", topicService.getById(id).getName());
        } catch (NoSuchElementException ex) {
            return "redirect:/error";
        }

        return "main/questions";
    }

    @GetMapping("/topic/{id}/questions/search")
    public String getPageByTopicAndName(@PathVariable int id,
                                        @RequestParam(name = "query") String text,
                                        @RequestParam(name = "page",
                                                required = false) Integer page,
                                        Model model) {
        if (page == null) {
            page = 1;
        } else if (page < 1) {
            return "redirect:/error";
        }

        try {
            PageDto<Question> questions = questionService.getPageByTopicAndName(text, id, page, 10);
            model.addAttribute("questions", questions.getElements());
            model.addAttribute("currentPage", questions.getCurrentPage());
            model.addAttribute("totalPages", questions.getTotalPages());
            model.addAttribute("statistics", questionService.getStatistic(questions.getElements()));
            model.addAttribute("isSearch", true);
            model.addAttribute("text", text);
            model.addAttribute("topicName", topicService.getById(id).getName());
        } catch (NoSuchElementException ex) {
            return "redirect:/error";
        }

        return "main/questions";
    }

    @GetMapping("/user/questions/{page}")
    public String getPageByUser(@PathVariable int page,
                                @RequestParam(name = "error",
                                        required = false) String error,
                                Model model) {
        if (page < 1) {
            return "redirect:/error";
        }

        PageDto<Question> questions = questionService.getPageByUser(
                userService.getCurrentLoggedIn(), page, 10);

        model.addAttribute("questions", questions.getElements());
        model.addAttribute("currentPage", questions.getCurrentPage());
        model.addAttribute("totalPages", questions.getTotalPages());
        model.addAttribute("statistics", questionService.getStatistic(questions.getElements()));
        model.addAttribute("topicNames", topicService.
                getTopicNamesByQuestions(questions.getElements()));

        if (error != null && (error.equals("delete") || error.equals("edit"))) {
            model.addAttribute("error", "You can't do this action because " +
                    "this question because it's used somewhere else");
        }

        return "user/questions";
    }

    @GetMapping("/user/questions/search")
    public String getPageByUserAndName(@RequestParam(name = "query") String text,
                                       @RequestParam(name = "page",
                                               required = false) Integer page,
                                       Model model) {
        if (page == null) {
            page = 1;
        } else if (page < 1) {
            return "redirect:/error";
        }

        PageDto<Question> questions = questionService.getPageByUserAndName(
                userService.getCurrentLoggedIn(), text, page, 10);
        model.addAttribute("questions", questions.getElements());
        model.addAttribute("currentPage", questions.getCurrentPage());
        model.addAttribute("totalPages", questions.getTotalPages());
        model.addAttribute("isSearch", true);
        model.addAttribute("text", text);
        model.addAttribute("statistics", questionService.getStatistic(questions.getElements()));
        model.addAttribute("topicNames", topicService
                .getTopicNamesByQuestions(questions.getElements()));

        return "user/questions";
    }

    private String getRedirectUrlToQuestionPage(User user, Integer topicId) {
        switch (user.getRole()) {
            case USER:
                return "/user/questions/1";
            case ADMIN:
                return "/admin/topic/" + topicId + "/questions/1";
        }
        return null;
    }
}
