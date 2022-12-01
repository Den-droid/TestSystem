package com.example.project.controllers;

import com.example.project.models.services.TestService;
import com.example.project.models.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class TestController {
    private final UserService userService;
    private final TestService testService;

    public TestController(UserService userService,
                          TestService testService) {
        this.testService = testService;
        this.userService = userService;
    }

    @GetMapping("/tests/generate")
    public String getGeneratePage() {
        return null;
    }

    @PostMapping("/tests/generate")
    public String generateTest() {
        return null;
    }

    @GetMapping("/user/{username}/tests")
    public String redirectToUserTestPage(@PathVariable String username) {
        String url = "/user/" + username + "/tests/" + 1;
        return "redirect:" + url;
    }

    @GetMapping("/user/{username}/tests/{page}")
    public String getUsersTestPage(@PathVariable String username,
                                   @PathVariable int page,
                                   @RequestParam(name = "type") String type,
                                   Model model) {
        return null;
    }
}
