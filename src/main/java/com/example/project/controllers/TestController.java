package com.example.project.controllers;

import com.example.project.dto.page.PageDto;
import com.example.project.dto.test.AddTestDto;
import com.example.project.models.entities.Test;
import com.example.project.models.entities.Topic;
import com.example.project.models.entities.User;
import com.example.project.models.enums.Role;
import com.example.project.models.services.TestService;
import com.example.project.models.services.TopicService;
import com.example.project.models.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Controller
public class TestController {
    private final UserService userService;
    private final TestService testService;
    private final TopicService topicService;

    public TestController(UserService userService,
                          TestService testService,
                          TopicService topicService) {
        this.testService = testService;
        this.userService = userService;
        this.topicService = topicService;
    }

    @GetMapping("/tests/generate")
    public String getAddPage(Model model) {
        model.addAttribute("difficulties", testService.getTestDifficulties());
        model.addAttribute("currentDate", LocalDateTime.now());
        return "test/generate";
    }

    @PostMapping("/tests/generate")
    public String add(@ModelAttribute(name = "generateTest") AddTestDto addTestDto,
                      Model model) {
        if (!Objects.equals(addTestDto.getAction(), "submit")) {
            if (addTestDto.getAction().equals("searchTopics")) {
                List<Topic> topics = topicService.getByNameContainsAndNamesNot(
                        addTestDto.getTopicPart(), addTestDto.getTopics());
                model.addAttribute("newTopics", topics);
            }

            if (addTestDto.getAction().equals("searchUsers")) {
                List<User> users = userService.getByUsernameContainsAndUsernamesNotInAndRole(
                        addTestDto.getUsernamePart(), addTestDto.getUsernames(), Role.USER);
                model.addAttribute("newUsers", users);
            }

            model.addAttribute("difficulties", testService.getTestDifficulties());
            model.addAttribute("currentDate", LocalDateTime.now());
            model.addAttribute("previous", addTestDto);
            return "test/generate";
        }

        User user = userService.getCurrentLoggedIn();
        testService.add(user, addTestDto);

        String url = "/user/" + user.getUsername() + "/tests";
        return "redirect:" + url;
    }

    @GetMapping("/test/{testId}/intro")
    public String getIntroPage(@PathVariable String testId,
                               Model model) {
        try {
            User user = userService.getCurrentLoggedIn();
            if (testService.canWalkthrough(user, testId)) {
                Test test = testService.getById(testId);
                model.addAttribute("test", test);
                model.addAttribute("testTopics", testService.getTestTopics(test));
            } else {
                String url = "/user/" + user.getUsername() + "/tests";
                return "redirect:" + url;
            }
        } catch (NoSuchElementException ex) {
            return "redirect:/error";
        }

        return "test/intro";
    }

    @PostMapping("/test/{testId}/intro")
    public String start(@PathVariable String testId) {
        try {
            testService.start(userService.getCurrentLoggedIn(), testId);
        } catch (NoSuchElementException ex) {
            return "redirect:/error";
        }

        String walkthroughUrl = "/test/" + testId + "/walkthrough";
        return "redirect:" + walkthroughUrl;
    }

    @GetMapping("/test/{testId}/walkthrough")
    public String getWalkthroughPage(@PathVariable String testId,
                                     Model model) {
        try {
            User user = userService.getCurrentLoggedIn();
            if (testService.canWalkthrough(user, testId)) {
                // To write
            } else {
                String url = "/user/" + user.getUsername() + "/tests";
                return "redirect:" + url;
            }
        } catch (NoSuchElementException ex) {
            return "redirect:/error";
        }

        return "test/walkthrough";
    }

    @PostMapping("/test/{testId}/walkthrough")
    public String finish(@PathVariable String testId) {
        return null;
    }

    @GetMapping("/test/{testId}/user/{username}/results")
    public String getResultsPage(@PathVariable String testId,
                                 @PathVariable String username,
                                 Model model) {
        return null;
    }

    @GetMapping("/user/{username}/tests")
    public String redirectToUserTestPage(@PathVariable String username) {
        String url = "/user/" + username + "/tests/" + 1;
        return "redirect:" + url;
    }

    @GetMapping("/user/{username}/tests/{page}")
    public String getByUsernameAndPageAndType(@PathVariable String username,
                                              @PathVariable int page,
                                              @RequestParam(name = "type", required = false) String type,
                                              Model model) {
        try {
            PageDto<Test> tests = testService.getByUser(type, username, page, 10);
            model.addAttribute("tests", tests.getElements());
            model.addAttribute("currentPage", tests.getCurrentPage());
            model.addAttribute("totalPages", tests.getTotalPages());
        } catch (IllegalArgumentException ex) {
            return "redirect:/error";
        }

        return "user/tests";
    }
}
