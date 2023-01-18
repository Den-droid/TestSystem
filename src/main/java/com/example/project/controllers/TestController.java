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
import com.example.project.models.enums.TestType;
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
    private static final String ERROR_URL = "/error";
    private static final String REDIRECT = "redirect:";
    private static final String NOT_ASSIGNED_ERROR_MESSAGE =
            "You are not assigned to this test!!!";
    private static final String NOT_USER_CREATED_ERROR_MESSAGE =
            "You did not create this test!!!";
    private static final String TOO_EARLY_TO_START_ERROR_MESSAGE =
            "Too early to start test!!!";

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
                        addTestDto.getUsernamePart(), addTestDto.getUsernames(), Role.USER);
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
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("difficulties", testService.getTestDifficulties());
            model.addAttribute("currentDate", now);
            model.addAttribute("previous", addTestDto);
            return "test/generate";
        }

        String url = "/user/tests";
        return REDIRECT + url;
    }

    @GetMapping("/test/{testId}/intro")
    public String getIntroPage(@PathVariable String testId,
                               Model model) {
        try {
            User user = userService.getCurrentLoggedIn();
            boolean hasStarted = testService.hasStarted(user, testId);
            boolean isTooEarly = testService.isTestTooEarly(testId);
            if (isTooEarly) {
                model.addAttribute("error", TOO_EARLY_TO_START_ERROR_MESSAGE);
            } else if (hasStarted) {
                String url = "/test/" + testId + "/walkthrough";
                return REDIRECT + url;
            }

            if (testService.canWalkthrough(user, testId)) {
                Test test = testService.getById(testId);
                model.addAttribute("test", testService.getIntro(test));
            } else {
                String url = "/user/tests/1";
                return REDIRECT + setParameterInUrl(url, "error", "notAssigned");
            }
        } catch (NoSuchElementException ex) {
            return REDIRECT + ERROR_URL;
        }

        return "test/intro";
    }

    @PostMapping("/test/{testId}/intro")
    public String start(@PathVariable String testId) {
        try {
            testService.start(userService.getCurrentLoggedIn(), testId);
        } catch (NoSuchElementException ex) {
            return REDIRECT + ERROR_URL;
        }

        String walkthroughUrl = "/test/" + testId + "/walkthrough";
        return REDIRECT + walkthroughUrl;
    }

    @GetMapping("/test/{testId}/walkthrough")
    public String getWalkthroughPage(@PathVariable String testId,
                                     @RequestParam(name = "question",
                                             required = false) Integer number,
                                     Model model) {
        if (number == null) {
            number = 1;
        }

        try {
            User user = userService.getCurrentLoggedIn();

            boolean hasFinished = testService.hasFinished(user, testId);
            boolean isTestOutdated = testService.isTestOutdated(testId);
            boolean hasStarted = testService.hasStarted(user, testId);
            if (isTestOutdated) {
                testService.finish(user, testId);
                String url = "/test/" + testId + "/results";
                return REDIRECT + setParameterInUrl(url, "user", user.getUsername());
            } else if (hasFinished) {
                String url = "/test/" + testId + "/results";
                return REDIRECT + setParameterInUrl(url, "user", user.getUsername());
            } else if (!hasStarted) {
                String url = "/test/" + testId + "/intro";
                return REDIRECT + url;
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
            return REDIRECT + ERROR_URL;
        }

        return "test/walkthrough";
    }

    @PostMapping("/test/{testId}/walkthrough")
    public String finish(@PathVariable String testId,
                         @ModelAttribute("testWalkthrough") TestWalkthroughDto dto) {
        User user = userService.getCurrentLoggedIn();

        testService.saveAnswer(user, testId, dto);

        if (!dto.getAction().equals("submit")) {
            String url = "/test/" + testId + "/walkthrough";
            if (dto.getAction().equals("previous")) {
                url = setParameterInUrl(url, "question", dto.getPreviousNumber().toString());
            } else {
                url = setParameterInUrl(url, "question", dto.getNextNumber().toString());
            }
            return REDIRECT + url;
        }

        testService.finish(user, testId);
        questionService.setStatistic(testId, user);
        questionService.changeCoefficient(testId);

        String url = "/test/" + testId + "/results";
        url = setParameterInUrl(url, "user", user.getUsername());
        return REDIRECT + url;
    }

    @GetMapping("/test/{testId}/results")
    public String getAllResultsPage(@PathVariable String testId,
                                    @RequestParam(name = "user",
                                            required = false) String username,
                                    Model model) {
        try {
            User user = userService.getCurrentLoggedIn();
            boolean isUserCreated = testService.isUserCreated(testId,
                    userService.getCurrentLoggedIn());

            if (username == null) {
                if (isUserCreated) {
                    model.addAttribute("resultsInfo", testService.getResultInfo(testId));
                    model.addAttribute("testId", testId);
                    return "test/resultInfo";
                } else {
                    String url = "/user/tests/1";
                    url = setParameterInUrl(url, "error", "notUserCreated");
                    return REDIRECT + url;
                }
            } else {
                if (user.getUsername().equals(username) || isUserCreated) {
                    boolean hasFinished = testService.hasFinished(
                            userService.getByUsername(username), testId);
                    if (hasFinished) {
                        model.addAttribute("testResult", testService.getUserTestResult(
                                testId, user));
                        return "test/result";
                    } else {
                        String url = "/test/" + testId + "/walkthrough";
                        return REDIRECT + url;
                    }
                } else {
                    return REDIRECT + ERROR_URL;
                }
            }
        } catch (NoSuchElementException ex) {
            return REDIRECT + ERROR_URL;
        }
    }

    @GetMapping("/user/tests")
    public String redirectToUserTestPage() {
        String url = "/user/tests/" + 1;
        return REDIRECT + url;
    }

    @GetMapping("/user/tests/{page}")
    public String getPageByUserAndType(@PathVariable int page,
                                       @RequestParam(name = "type",
                                               required = false) String type,
                                       @RequestParam(name = "error",
                                               required = false) String error,
                                       Model model) {
        if (page < 1) {
            return REDIRECT + ERROR_URL;
        }

        try {
            PageDto<Test> testPage = testService.getPage(type, null,
                    userService.getCurrentLoggedIn(), page, 10);

            List<String> testTypes = testService.getTestTypes();
            model.addAttribute("testPage", testPage);
            model.addAttribute("testTypes", testTypes);
            model.addAttribute("chosenTestType", type == null
                    ? TestType.ASSIGNED.getText() : type);
            model.addAttribute("username", userService.getCurrentLoggedIn().getUsername());
            model.addAttribute("isTests", true);
            if (error != null) {
                if (error.equals("notAssigned")) {
                    model.addAttribute("error", NOT_ASSIGNED_ERROR_MESSAGE);
                } else if (error.equals("notUserCreated")) {
                    model.addAttribute("error", NOT_USER_CREATED_ERROR_MESSAGE);
                }
            }
        } catch (IllegalArgumentException ex) {
            return REDIRECT + ERROR_URL;
        }

        return "user/tests";
    }

    @GetMapping("/user/tests/search")
    public String getPageByUserAndTypeAndName(@RequestParam(name = "query") String name,
                                              @RequestParam(name = "type",
                                                      required = false) String type,
                                              @RequestParam(name = "page",
                                                      required = false) Integer page,
                                              Model model) {
        if (page == null) {
            page = 1;
        } else if (page < 1) {
            return REDIRECT + ERROR_URL;
        }

        try {
            PageDto<Test> testPage = testService.getPage(type, name,
                    userService.getCurrentLoggedIn(), page, 10);

            List<String> testTypes = testService.getTestTypes();
            model.addAttribute("testPage", testPage);
            model.addAttribute("testTypes", testTypes);
            model.addAttribute("chosenTestType", type == null
                    ? TestType.ASSIGNED.getText() : type);
            model.addAttribute("username", userService.getCurrentLoggedIn().getUsername());
            model.addAttribute("isTests", true);
            model.addAttribute("isSearch", true);
            model.addAttribute("name", name);
        } catch (IllegalArgumentException ex) {
            return REDIRECT + ERROR_URL;
        }

        return "user/tests";
    }

    private String setParameterInUrl(String initialString, String paramName, String paramValue) {
        return initialString + "?" + paramName + "=" + paramValue;
    }
}
