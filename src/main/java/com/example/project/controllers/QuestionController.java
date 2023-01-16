package com.example.project.controllers;

import com.example.project.dto.page.PageDto;
import com.example.project.dto.question.AddQuestionDto;
import com.example.project.dto.question.AddQuestionPageDto;
import com.example.project.dto.question.EditQuestionDto;
import com.example.project.dto.question.EditQuestionPageDto;
import com.example.project.models.entities.Question;
import com.example.project.models.entities.User;
import com.example.project.models.enums.Role;
import com.example.project.models.services.QuestionService;
import com.example.project.models.services.TopicService;
import com.example.project.models.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

@Controller
public class QuestionController {
    private final QuestionService questionService;
    private final TopicService topicService;
    private final UserService userService;
    private static final String ERROR_URL = "/error";
    private static final String REDIRECT = "redirect:";
    private static final String EDIT_DELETE_QUESTION_ERROR_MESSAGE =
            "You can't do this action because this question because it's used somewhere else!!!";

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
            return REDIRECT + ERROR_URL;
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
            return REDIRECT + ERROR_URL;
        }

        return REDIRECT + getUrlToQuestionPage(user, null);
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
                model.addAttribute("answers", dto.getAnswers());
            } else {
                User user = userService.getCurrentLoggedIn();
                return REDIRECT + setErrorInUrl(getUrlToQuestionPage(user, null), "edit");
            }
        } catch (NoSuchElementException ex) {
            return REDIRECT + ERROR_URL;
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
        return REDIRECT + getUrlToQuestionPage(user, null);
    }

    @GetMapping("/questions/delete/{questionId}")
    public String delete(@PathVariable long questionId) throws IOException {
        String url = "";

        try {
            User user = userService.getCurrentLoggedIn();
            url = getUrlToQuestionPage(user, topicService.getIdByQuestionId(questionId));

            questionService.delete(questionId);
        } catch (NoSuchElementException ex) {
            return REDIRECT + ERROR_URL;
        } catch (IllegalArgumentException ex) {
            url = setErrorInUrl(url, "delete");
        }

        return REDIRECT + url;
    }

    @GetMapping("/admin/topic/{id}/questions")
    public String redirectToAdminPage(@PathVariable int id) {
        String url = "/admin/topic/" + id + "/questions/" + 1;
        return REDIRECT + url;
    }

    @GetMapping("/topic/{id}/questions")
    public String redirectToPage(@PathVariable int id) {
        String url = "/topic/" + id + "/questions/" + 1;
        return REDIRECT + url;
    }

    @GetMapping("/user/questions")
    public String redirectToUserPage() {
        String url = "/user/questions/" + 1;
        return REDIRECT + url;
    }

    @GetMapping("/admin/topic/{id}/questions/{page}")
    public String getPageByTopicForAdmin(@PathVariable int id,
                                         @PathVariable int page,
                                         @RequestParam(name = "error",
                                                 required = false) String error,
                                         Model model) {
        if (page < 1) {
            return REDIRECT + ERROR_URL;
        }

        try {
            PageDto<Question> questionPage = questionService.getPageByTopic(id, page, 10);

            model.addAttribute("questionPage", questionPage);
            model.addAttribute("topicName", topicService.getById(id).getName());
        } catch (NoSuchElementException ex) {
            return REDIRECT + ERROR_URL;
        }

        if (error != null && (error.equals("delete") || error.equals("edit"))) {
            model.addAttribute("error", EDIT_DELETE_QUESTION_ERROR_MESSAGE);
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
            return REDIRECT + ERROR_URL;
        }

        try {
            PageDto<Question> questionPage =
                    questionService.getPageByTopicAndName(text, id, page, 10);

            model.addAttribute("questionPage", questionPage);
            model.addAttribute("isSearch", true);
            model.addAttribute("text", text);
            model.addAttribute("topicName", topicService.getById(id).getName());
        } catch (NoSuchElementException ex) {
            return REDIRECT + ERROR_URL;
        }

        return "admin/questions";
    }

    @GetMapping("/topic/{id}/questions/{page}")
    public String getPageByTopic(@PathVariable int page,
                                 @PathVariable int id,
                                 Model model) {
        if (page < 1) {
            return REDIRECT + ERROR_URL;
        }

        try {
            PageDto<Question> questionPage = questionService.getPageByTopic(id, page, 10);

            model.addAttribute("questionPage", questionPage);
            model.addAttribute("topicName", topicService.getById(id).getName());
        } catch (NoSuchElementException ex) {
            return REDIRECT + ERROR_URL;
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
            return REDIRECT + ERROR_URL;
        }

        try {
            PageDto<Question> questionPage =
                    questionService.getPageByTopicAndName(text, id, page, 10);

            model.addAttribute("questionPage", questionPage);
            model.addAttribute("text", text);
            model.addAttribute("isSearch", true);
            model.addAttribute("topicName", topicService.getById(id).getName());
        } catch (NoSuchElementException ex) {
            return REDIRECT + ERROR_URL;
        }

        return "main/questions";
    }

    @GetMapping("/user/questions/{page}")
    public String getPageByUser(@PathVariable int page,
                                @RequestParam(name = "error",
                                        required = false) String error,
                                Model model) {
        if (page < 1) {
            return REDIRECT + ERROR_URL;
        }

        PageDto<Question> questionPage = questionService.getPageByUser(
                userService.getCurrentLoggedIn(), page, 10);
        List<String> topicNames = topicService.
                getTopicNamesByQuestions(questionPage.getElements());

        model.addAttribute("questionPage", questionPage);
        model.addAttribute("topicNames", topicNames);

        if (error != null && (error.equals("delete") || error.equals("edit"))) {
            model.addAttribute("error", EDIT_DELETE_QUESTION_ERROR_MESSAGE);
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
            return REDIRECT + ERROR_URL;
        }

        PageDto<Question> questionPage = questionService.getPageByUserAndName(
                userService.getCurrentLoggedIn(), text, page, 10);
        List<String> topicNames = topicService.
                getTopicNamesByQuestions(questionPage.getElements());

        model.addAttribute("questionPage", questionPage);
        model.addAttribute("isSearch", true);
        model.addAttribute("text", text);
        model.addAttribute("topicNames", topicNames);

        return "user/questions";
    }

    private String getUrlToQuestionPage(User user, Integer topicId) {
        if (user.getRole().equals(Role.USER)) {
            return "/user/questions/1";
        } else if (user.getRole().equals(Role.ADMIN)) {
            return "/admin/topic/" + topicId + "/questions/1";
        }
        return null;
    }

    private String setErrorInUrl(String initialString, String errorLabel) {
        return initialString + "?error=" + errorLabel;
    }
}
