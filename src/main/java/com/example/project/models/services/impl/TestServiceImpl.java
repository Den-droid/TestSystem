package com.example.project.models.services.impl;

import com.example.project.dto.page.PageDto;
import com.example.project.dto.test.AddTestDto;
import com.example.project.dto.test.TestQuestionDto;
import com.example.project.dto.test.TestWalkthroughDto;
import com.example.project.models.entities.*;
import com.example.project.models.enums.AnswerType;
import com.example.project.models.enums.TestDifficulty;
import com.example.project.models.enums.TestType;
import com.example.project.models.mappers.AddTestMapper;
import com.example.project.models.repositories.*;
import com.example.project.models.services.TestService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TestServiceImpl implements TestService {
    private final TestRepository testRepository;
    private final CurrentTestRepository currentTestRepository;
    private final FinishedTestRepository finishedTestRepository;
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;
    private final TestQuestionRepository testQuestionRepository;

    public TestServiceImpl(TestRepository testRepository,
                           CurrentTestRepository currentTestRepository,
                           FinishedTestRepository finishedTestRepository,
                           UserRepository userRepository,
                           TopicRepository topicRepository,
                           TestQuestionRepository testQuestionRepository) {
        this.finishedTestRepository = finishedTestRepository;
        this.testRepository = testRepository;
        this.currentTestRepository = currentTestRepository;
        this.userRepository = userRepository;
        this.topicRepository = topicRepository;
        this.testQuestionRepository = testQuestionRepository;
    }

    @Override
    public void add(User user, AddTestDto dto) {
        Test test = AddTestMapper.map(dto);
        test.setUserCreated(user);

        Set<Topic> topics = new LinkedHashSet<>();
        int questionsCount = 0;
        for (String topicName : dto.getTopics()) {
            Topic topic = topicRepository.findByName(topicName);
            questionsCount += topic.getQuestions().size();
            topics.add(topic);
        }
        if (questionsCount < test.getQuestionsCount())
            throw new IllegalArgumentException();

        test.setTopics(topics);

        Set<User> users = new LinkedHashSet<>();
        for (String username : dto.getUsernames()) {
            User assignedUser = userRepository.findByUsernameIgnoreCase(username);
            users.add(assignedUser);
        }
        if (dto.getIncludeMe() != null)
            users.add(user);
        test.setUsersAssigned(users);

        generate(test);
        testRepository.save(test);
    }

    @Override
    public void start(User user, String testId) {
        CurrentTest currentTest = new CurrentTest();
        currentTest.setUser(user);
        currentTest.setTest(testRepository.findById(testId)
                .orElseThrow(NoSuchElementException::new));
        currentTest.setDateStarted(LocalDateTime.now());
        currentTestRepository.save(currentTest);
    }

    @Override
    public void finish(User user, String testId, TestWalkthroughDto dto) {

    }

    @Override
    public PageDto<Test> getByUser(String type, String username, int page, int limit) {
        TestType testType = TestType.ALL;
        if (type != null)
            testType = TestType.getByText(type);
        switch (testType) {
            case ALL:
                Page<Test> tests = getAssignedToUserByUsername(username, page, limit);
                List<Test> testList = tests.getContent();
                return new PageDto<>(testList, page, tests.getTotalPages());
            case FINISHED:
                Page<CurrentTest> currentTests = getCurrentOfUserByUsername(username, page, limit);
                List<Test> currentTestsList = currentTests.getContent().stream()
                        .map(CurrentTest::getTest)
                        .collect(Collectors.toList());
                return new PageDto<>(currentTestsList, page, currentTests.getTotalPages());
            case CURRENT:
                Page<FinishedTest> finishedTests = getFinishedByUsername(username, page, limit);
                List<Test> finishedTestsList = finishedTests.getContent().stream()
                        .map(FinishedTest::getTest)
                        .collect(Collectors.toList());
                return new PageDto<>(finishedTestsList, page, finishedTests.getTotalPages());
            case CREATED:
                Page<Test> userCreatedTest = getCreatedByUser(username, page, limit);
                List<Test> userCreatedTestsList = userCreatedTest.getContent();
                return new PageDto<>(userCreatedTestsList, page, userCreatedTest.getTotalPages());
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public Test getById(String id) {
        return testRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);
    }

    @Override
    public boolean canWalkthrough(User user, String testId) {
        Test test = testRepository.findById(testId).orElseThrow(NoSuchElementException::new);
        Set<Test> testAssigned = user.getAssignedTests();

        List<FinishedTest> finishedTests = user.getFinishedTests();
        List<Test> finishedTestsList = finishedTests.stream()
                .map(FinishedTest::getTest)
                .collect(Collectors.toList());

        return testAssigned.contains(test) && !finishedTestsList.contains(test);
    }

    @Override
    public List<TestQuestionDto> getTestQuestions(String testId) {
        Test test = testRepository.findById(testId)
                .orElseThrow(NoSuchElementException::new);
        return getTestQuestions(test);
    }

    @Override
    public Set<Topic> getTestTopics(Test test) {
        return test.getTopics();
    }

    @Override
    public List<String> getTestDifficulties() {
        return Arrays.stream(TestDifficulty.values())
                .map(TestDifficulty::getText)
                .collect(Collectors.toList());
    }

    private List<TestQuestionDto> getTestQuestions(Test test) {
        List<TestQuestion> testQuestions = test.getQuestions();
        List<TestQuestionDto> testQuestionDto = new ArrayList<>(testQuestions.size());

        for (TestQuestion testQuestion : testQuestions) {
            Question question = testQuestion.getQuestion();
            TestQuestionDto dto = new TestQuestionDto();
            dto.setQuestionId(question.getId());
            dto.setQuestionText(question.getText());
            dto.setQuestionMedia(question.getMediaUrl());
            dto.setQuestionAnswerDescription(question.getAnswerDescription());
            dto.setAnswerType(question.getAnswerType().getText());
            if (question.getAnswerType().equals(AnswerType.MATCH)) {
                List<Question> subQuestions = question.getSubQuestions();

                List<String> subQuestionText = new ArrayList<>(subQuestions.size());
                subQuestionText.addAll(
                        subQuestions.stream()
                                .map(Question::getText)
                                .collect(Collectors.toList())
                );
                dto.setSubQuestionsText(subQuestionText);

                List<Answer> subQuestionAnswers = subQuestions.stream()
                        .map(Question::getAnswers)
                        .collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);
                List<String> answersText = new ArrayList<>(subQuestions.size());
                answersText.addAll(
                        subQuestionAnswers.stream()
                                .map(Answer::getText)
                                .collect(Collectors.toList())
                );
                dto.setAnswers(answersText);
            }

            if (question.getAnswerType().equals(AnswerType.SINGLE) ||
                    question.getAnswerType().equals(AnswerType.MULTIPLE)) {
                List<Answer> answers = question.getAnswers();
                List<String> answersText = new ArrayList<>(answers.size());
                answersText.addAll(
                        answers.stream()
                                .map(Answer::getText)
                                .collect(Collectors.toList())
                );
                dto.setAnswers(answersText);
            }

            testQuestionDto.add(dto);
        }

        return testQuestionDto;
    }

    private void generate(Test test) {
        Set<Topic> topics = test.getTopics();

        List<Question> questions = new ArrayList<>();
        for (Topic topic : topics) {
            questions.addAll(topic.getQuestions());
        }

        List<TestQuestion> testQuestions = new ArrayList<>(test.getQuestionsCount());

        double sumCoefficients = 0.0;
        double minDifference;
        Question addToTest = questions.get(0);
        for (int i = 0; i < test.getQuestionsCount(); i++) {
            minDifference = 1.0;
            for (Question question : questions) {
                double tmpDifference = Math.abs(question.getDifficulty().getCoefficient()
                        - test.getDifficulty().getCoefficient());
                if (tmpDifference < minDifference) {
                    addToTest = question;
                    minDifference = tmpDifference;
                }
            }
            sumCoefficients += addToTest.getDifficulty().getCoefficient()
                    * addToTest.getCoefficient();

            TestQuestion testQuestion = new TestQuestion();
            testQuestion.setQuestion(addToTest);
            testQuestion.setTest(test);
            testQuestions.add(testQuestion);

            questions.remove(addToTest);
        }

        for (TestQuestion testQuestion : testQuestions) {
            Question question = testQuestion.getQuestion();
            double value = ((question.getDifficulty().getCoefficient() * question.getCoefficient())
                    / sumCoefficients) * 100;
            testQuestion.setValue(value);
        }

        test.setQuestions(testQuestions);
    }

    private Page<Test> getAssignedToUserByUsername(String username, int page, int limit) {
        return testRepository.findTestsByUsersAssignedUsername(
                username, PageRequest.of(page - 1, limit));
    }

    private Page<FinishedTest> getFinishedByUsername(String username, int page, int limit) {
        return finishedTestRepository.findFinishedTestsByUser(
                userRepository.findByUsernameIgnoreCase(username), PageRequest.of(page - 1, limit)
        );
    }

    private Page<CurrentTest> getCurrentOfUserByUsername(String username, int page, int limit) {
        return currentTestRepository.findCurrentTestsByUser(
                userRepository.findByUsernameIgnoreCase(username), PageRequest.of(page - 1, limit)
        );
    }

    private Page<Test> getCreatedByUser(String username, int page, int limit) {
        return testRepository.findAllByUserCreated(
                userRepository.findByUsernameIgnoreCase(username), PageRequest.of(page - 1, limit));
    }
}