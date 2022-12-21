package com.example.project.controllers;

import com.example.project.dto.page.PageDto;
import com.example.project.dto.test.AddTestDto;
import com.example.project.dto.test.TestAnswerDto;
import com.example.project.dto.test.TestQuestionDto;
import com.example.project.dto.test.TestWalkthroughDto;
import com.example.project.models.entities.Test;
import com.example.project.models.entities.Topic;
import com.example.project.models.entities.User;
import com.example.project.models.enums.Role;
import com.example.project.models.services.QuestionService;
import com.example.project.models.services.TestService;
import com.example.project.models.services.TopicService;
import com.example.project.models.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Controller
public class TestController {
    private final UserService userService;
    private final TestService testService;
    private final TopicService topicService;
    private final QuestionService questionService;

    public TestController(UserService userService,
                          TestService testService,
                          TopicService topicService,
                          QuestionService questionService) {
        this.testService = testService;
        this.userService = userService;
        this.topicService = topicService;
        this.questionService = questionService;
    }

    @GetMapping("/tests/generate")
    public String getAddPage(Model model) {
        LocalDateTime now = LocalDateTime.now().plusMinutes(1)
                .withSecond(0).withNano(0);
        model.addAttribute("difficulties", testService.getTestDifficulties());
        model.addAttribute("currentDate", now);
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
                        addTestDto.getUsernamePart(), addTestDto.getUsernames(),
                        addTestDto.getIncludeMe(), Role.USER);
                model.addAttribute("newUsers", users);
            }

            LocalDateTime now = LocalDateTime.now().plusMinutes(1)
                    .withSecond(0).withNano(0);

            model.addAttribute("difficulties", testService.getTestDifficulties());
            model.addAttribute("currentDate", now);
            model.addAttribute("previous", addTestDto);
            return "test/generate";
        }

        User user = userService.getCurrentLoggedIn();
        try {
            testService.add(user, addTestDto);
        } catch (IllegalArgumentException ex) {
            LocalDateTime now = LocalDateTime.now().plusMinutes(1)
                    .withSecond(0).withNano(0);
            model.addAttribute("error", "System doesn't have necessary amount " +
                    "of questions for this topic. Try another topics or add questions " +
                    "to topics you've chosen!!!");
            model.addAttribute("difficulties", testService.getTestDifficulties());
            model.addAttribute("currentDate", now);
            model.addAttribute("previous", addTestDto);
            return "test/generate";
        }

        String url = "/user/tests";
        return "redirect:" + url;
    }

    @GetMapping("/test/{testId}/intro")
    public String getIntroPage(@PathVariable String testId,
                               @RequestParam(name = "error", required = false) String error,
                               Model model) {
        try {
            User user = userService.getCurrentLoggedIn();
            boolean hasStarted = testService.hasStarted(user, testId);
            if (hasStarted) {
                String url = "/test/" + testId + "/walkthrough";
                return "redirect:" + url;
            }
            if (testService.canWalkthrough(user, testId)) {
                Test test = testService.getById(testId);
                model.addAttribute("test", testService.getIntro(test));
                if (error != null && error.equals("tooEarly")) {
                    model.addAttribute("error", "Too early to start test!!!");
                }
            } else {
                String url = "/user/tests/1?error=notAssigned";
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
                                     @RequestParam(name = "question", required = false) Integer number,
                                     Model model) {
        if (number == null) {
            number = 1;
        }

        try {
            User user = userService.getCurrentLoggedIn();

            boolean hasFinished = testService.hasFinished(user, testId);
            boolean isTestOutdated = testService.isTestOutdated(testId);
            boolean isTooEarly = testService.isTestTooEarly(testId);
            boolean hasStarted = testService.hasStarted(user, testId);
            if (isTooEarly) {
                String url = "/test/" + testId + "/intro?error=tooEarly";
                return "redirect:" + url;
            } else if (!hasStarted) {
                String url = "/test/" + testId + "/intro";
                return "redirect:" + url;
            } else if (isTestOutdated) {
                testService.finish(user, testId);
                String url = "/test/" + testId + "/results?user=" + user.getUsername();
                return "redirect:" + url;
            } else if (hasFinished) {
                String url = "/test/" + testId + "/results?user=" + user.getUsername();
                return "redirect:" + url;
            }

            TestQuestionDto question = testService.getTestQuestionByNumber(testId, number);
            TestAnswerDto answer = testService.getTestQuestionAnswerByUserAndNumber(user,
                    testId, number);
            model.addAttribute("question", question);
            model.addAttribute("answers", answer);
            model.addAttribute("hasTimeToComplete", true);
            model.addAttribute("isUnlimitedTime", false);

            LocalTime timeLeft = testService.getTimeLeft(user, testId);
            boolean isTimeUnlimited = testService.isTimeUnlimited(testId);
            model.addAttribute("timeLeft", timeLeft);
            if (isTimeUnlimited) {
                model.addAttribute("isUnlimitedTime", true);
            } else if (timeLeft.toSecondOfDay() == 0) {
                model.addAttribute("hasTimeToComplete", false);
            }
        } catch (NoSuchElementException | IllegalArgumentException ex) {
            return "redirect:/error";
        }

        return "test/walkthrough";
    }

    @PostMapping("/test/{testId}/walkthrough")
    public String finish(@PathVariable String testId,
                         @ModelAttribute("testWalkthrough") TestWalkthroughDto dto) {
        User user = userService.getCurrentLoggedIn();

        testService.saveAnswer(user, testId, dto);

        if (!dto.getAction().equals("submit")) {
            String url;
            if (dto.getAction().equals("previous")) {
                url = "/test/" + testId + "/walkthrough?question=" + dto.getPreviousNumber();
            } else {
                url = "/test/" + testId + "/walkthrough?question=" + dto.getNextNumber();
            }
            return "redirect:" + url;
        }

        testService.finish(user, testId);
        questionService.setStatistic(testId, user);
        questionService.changeCoefficient(testId);

        String url = "/test/" + testId + "/user/" + user.getUsername() + "/results";
        return "redirect:" + url;
    }

    @GetMapping("/test/{testId}/results")
    public String getAllResultsPage(@PathVariable String testId,
                                    @RequestParam(name = "user", required = false) String username,
                                    Model model) {
        try {
            if (username == null) {
                boolean isUserCreated = testService.isUserCreated(testId,
                        userService.getCurrentLoggedIn());
                if (isUserCreated) {
                    model.addAttribute("resultsInfo", testService.getResultInfo(testId));
                    model.addAttribute("testId", testId);
                    return "test/resultInfo";
                } else {
                    String url = "/user/tests/1?error=notUserCreated";
                    return "redirect:" + url;
                }
            } else {
                boolean hasFinished = testService.hasFinished(
                        userService.getByUsername(username), testId);
                if (hasFinished) {
                    model.addAttribute("testResult", testService.getUserTestResult(
                            testId, userService.getCurrentLoggedIn()));
                    return "test/result";
                } else {
                    String url = "/test/" + testId + "/walkthrough";
                    return "redirect:" + url;
                }
            }
        } catch (NoSuchElementException ex) {
            return "redirect:/error";
        }
    }

    @GetMapping("/user/tests")
    public String redirectToUserTestPage() {
        String url = "/user/tests/" + 1;
        return "redirect:" + url;
    }

    @GetMapping("/user/tests/{page}")
    public String getByUsernameAndPageAndType(@PathVariable int page,
                                              @RequestParam(name = "type", required = false) String type,
                                              @RequestParam(name = "error", required = false) String error,
                                              Model model) {
        try {
            PageDto<Test> tests = testService.getByUser(type,
                    userService.getCurrentLoggedIn().getUsername(), page, 10);
            List<String> testTypes = testService.getTestTypes();
            model.addAttribute("tests", tests.getElements());
            model.addAttribute("currentPage", tests.getCurrentPage());
            model.addAttribute("totalPages", tests.getTotalPages());
            model.addAttribute("testTypes", testTypes);
            model.addAttribute("chosenTestType", type);
            if (error != null && error.equals("notAssigned"))
                model.addAttribute("error", "You are not assigned to this test!!!");
        } catch (IllegalArgumentException ex) {
            return "redirect:/error";
        }

        return "user/tests";
    }
}
