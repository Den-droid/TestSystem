package com.example.project.models.services.impl;

import com.example.project.dto.page.PageDto;
import com.example.project.dto.test.AddTestDto;
import com.example.project.dto.test.TestAnswerDto;
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

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    private final TestAnswerRepository testAnswerRepository;
    private final QuestionRepository questionRepository;
    private final QuestionStatisticRepository questionStatisticRepository;
    private final AnswerRepository answerRepository;

    public TestServiceImpl(TestRepository testRepository,
                           CurrentTestRepository currentTestRepository,
                           FinishedTestRepository finishedTestRepository,
                           UserRepository userRepository,
                           TopicRepository topicRepository,
                           TestQuestionRepository testQuestionRepository,
                           TestAnswerRepository testAnswerRepository,
                           QuestionRepository questionRepository,
                           QuestionStatisticRepository questionStatisticRepository,
                           AnswerRepository answerRepository) {
        this.finishedTestRepository = finishedTestRepository;
        this.testRepository = testRepository;
        this.currentTestRepository = currentTestRepository;
        this.userRepository = userRepository;
        this.topicRepository = topicRepository;
        this.testQuestionRepository = testQuestionRepository;
        this.testAnswerRepository = testAnswerRepository;
        this.questionRepository = questionRepository;
        this.questionStatisticRepository = questionStatisticRepository;
        this.answerRepository = answerRepository;
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
    public void saveAnswer(User user, String testId, TestWalkthroughDto testWalkthroughDto) {
        Test test = testRepository.findById(testId)
                .orElseThrow(NoSuchElementException::new);
        Question question = questionRepository.findById(testWalkthroughDto.getQuestionId())
                .orElseThrow(NoSuchElementException::new);
        AnswerType answerType = AnswerType.getByText(testWalkthroughDto.getAnswerType());
        List<TestAnswer> testAnswers = testAnswerRepository.findByTestAndUserAndQuestion(test,
                user, question);

        if (answerType.equals(AnswerType.MULTIPLE)) {
            List<String> answers = testWalkthroughDto.getAnswers();
            if (testAnswers == null || testAnswers.size() == 0) {
                testAnswers = new ArrayList<>(testWalkthroughDto.getAnswers().size());
                for (String answer : answers) {
                    TestAnswer testAnswer = getTestAnswer(test, user, question, answer);
                    testAnswers.add(testAnswer);
                }
            } else {
                List<TestAnswer> testAnswersToRemove = testAnswerRepository
                        .findByTestAndUserAndQuestionAndAnswerNotIn(test, user, question, answers);
                if (testAnswersToRemove != null && testAnswersToRemove.size() != 0) {
                    testAnswerRepository.deleteAll(testAnswersToRemove);
                }
                List<TestAnswer> testAnswersExisted = testAnswerRepository
                        .findByTestAndUserAndQuestionAndAnswerIn(test, user, question, answers);
                List<String> testAnswersExistedString = testAnswersExisted.stream()
                        .map(TestAnswer::getAnswer)
                        .collect(Collectors.toList());
                answers.removeIf(testAnswersExistedString::contains);
                for (String answer : answers) {
                    TestAnswer testAnswer = getTestAnswer(test, user, question, answer);
                    testAnswers.add(testAnswer);
                }
            }
            testAnswerRepository.saveAll(testAnswers);
        } else if (answerType.equals(AnswerType.MATCH)) {
            List<String> subQuestionText = testWalkthroughDto.getSubQuestionText();
            List<Integer> subQuestionNumOfAnswers = testWalkthroughDto.getSubQuestionNumberOfAnswers();
            List<String> answers = testWalkthroughDto.getAnswers();
            testAnswers = new ArrayList<>(subQuestionNumOfAnswers.size());
            for (int i = 0; i < subQuestionNumOfAnswers.size(); i++) {
                Question matchQuestion = questionRepository.findBySupQuestionAndText(question,
                        subQuestionText.get(i));
                List<TestAnswer> matchQuestionAnswer = testAnswerRepository.
                        findByTestAndUserAndQuestion(test, user, matchQuestion);

                TestAnswer testAnswer;
                if (matchQuestionAnswer == null) {
                    testAnswer = getTestAnswer(test, user, matchQuestion,
                            answers.get(subQuestionNumOfAnswers.get(i)));
                } else {
                    testAnswer = matchQuestionAnswer.get(0);
                    testAnswer.setAnswer(answers.get(subQuestionNumOfAnswers.get(i)));
                }
                testAnswers.add(testAnswer);
            }
            testAnswerRepository.saveAll(testAnswers);
        } else {
            if (testAnswers == null || testAnswers.size() == 0) {
                TestAnswer testAnswer = getTestAnswer(test, user, question,
                        testWalkthroughDto.getAnswers().get(0));
                testAnswerRepository.save(testAnswer);
            } else {
                testAnswers.get(0).setAnswer(testWalkthroughDto.getAnswers().get(0));
                testAnswerRepository.saveAll(testAnswers);
            }
        }
    }

    @Override
    public void start(User user, String testId) {
        Test test = testRepository.findById(testId)
                .orElseThrow(NoSuchElementException::new);
        CurrentTest existed = currentTestRepository.findByUserAndTest(user, test);
        if (existed != null) {
            throw new IllegalArgumentException();
        }
        CurrentTest currentTest = new CurrentTest();
        currentTest.setUser(user);
        currentTest.setTest(test);
        currentTest.setDateStarted(LocalDateTime.now());
        currentTestRepository.save(currentTest);
    }

    @Override
    public void finish(User user, String testId) {
        Test test = testRepository.findById(testId)
                .orElseThrow(NoSuchElementException::new);

        double mark = getTestMarkAndSetStatistic(test, user);
        FinishedTest finishedTest = new FinishedTest();
        finishedTest.setTest(test);
        finishedTest.setUser(user);
        finishedTest.setMark(mark);

        changeCoefficient(test.getQuestions());
        List<Question> questions = test.getQuestions().stream()
                .map(TestQuestion::getQuestion)
                .collect(Collectors.toList());

        questionRepository.saveAll(questions);
        finishedTestRepository.save(finishedTest);
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
        return testAssigned.contains(test);
    }

    @Override
    public LocalTime getTimeLeft(User user, String testId) {
        Test test = testRepository.findById(testId).orElseThrow(NoSuchElementException::new);
        CurrentTest currentTest = currentTestRepository.findByUserAndTest(user, test);
        LocalDateTime dateStarted = currentTest.getDateStarted();
        LocalTime timeLimit = test.getTimeLimit();
        LocalDateTime now = LocalDateTime.now();

        Duration duration = Duration.between(dateStarted, now);
        Duration timeLeft = duration.minus(
                Duration.between(LocalTime.of(0, 0, 0), timeLimit));
        if (duration.isNegative() || duration.isZero()) {
            return LocalTime.of(0, 0, 0);
        } else {
            return LocalTime.of(0, 0, 0).plusNanos(timeLeft.toNanos());
        }
    }

    @Override
    public boolean hasStarted(User user, String testId) {
        Test test = testRepository.findById(testId).orElseThrow(NoSuchElementException::new);
        CurrentTest currentTest = currentTestRepository.findByUserAndTest(user, test);
        return currentTest != null;
    }

    @Override
    public boolean hasFinished(User user, String testId) {
        Test test = testRepository.findById(testId).orElseThrow(NoSuchElementException::new);
        FinishedTest finishedTest = finishedTestRepository.findByUserAndTest(user, test);
        return finishedTest != null;
    }

    @Override
    public TestQuestionDto getTestQuestionByNumber(String testId, int number) {
        Test test = testRepository.findById(testId)
                .orElseThrow(NoSuchElementException::new);
        if (number < 1 || number > test.getQuestionsCount())
            throw new IllegalArgumentException();
        return getTestQuestionByNumber(test, number);
    }

    @Override
    public TestAnswerDto getTestQuestionAnswerByUserAndNumber(User user, String testId, int number) {
        Test test = testRepository.findById(testId)
                .orElseThrow(NoSuchElementException::new);
        if (number < 1 || number > test.getQuestionsCount())
            throw new IllegalArgumentException();
        return getTestQuestionAnswerByUserAndNumber(test, user, number);
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

    private double getTestMarkAndSetStatistic(Test test, User user) {
        double mark = 0.0;
        List<TestAnswer> testAnswers = testAnswerRepository.findByTestAndUser(test, user);
        List<TestQuestion> testQuestions = test.getQuestions();

        for (TestQuestion testQuestion : testQuestions) {
            Question question = testQuestion.getQuestion();
            boolean isCorrect = true;
            if (question.getAnswerType().equals(AnswerType.MATCH)) {
                int correctAnswersCount = 0;
                List<Question> matchQuestions = questionRepository.findAllBySupQuestion(question);
                for (TestAnswer testAnswer : testAnswers) {
                    Question testAnswerQuestion = testAnswer.getQuestion();
                    String correctAnswer = matchQuestions.stream()
                            .filter(x -> x.equals(testAnswerQuestion))
                            .map(Question::getAnswers)
                            .map(x -> x.get(0))
                            .map(Answer::getText)
                            .findFirst()
                            .orElseThrow(NoSuchElementException::new);
                    if (correctAnswer.equals(testAnswer.getAnswer()))
                        correctAnswersCount++;
                    else
                        isCorrect = false;
                }
                if (correctAnswersCount == matchQuestions.size())
                    mark += testQuestion.getValue();
                else {
                    mark += testQuestion.getValue() *
                            ((correctAnswersCount + 0.0) / matchQuestions.size());
                }
            } else {
                List<String> correctAnswers = question.getAnswers().stream()
                        .filter(Answer::isCorrect)
                        .map(Answer::getText)
                        .collect(Collectors.toList());
                List<String> questionAnswers = testAnswers.stream()
                        .filter(x -> x.getQuestion().equals(question))
                        .map(TestAnswer::getAnswer)
                        .collect(Collectors.toList());
                if (question.getAnswerType().equals(AnswerType.SINGLE)) {
                    String correctAnswer = correctAnswers.get(0);
                    String questionAnswer = questionAnswers.get(0);
                    isCorrect = correctAnswer.equals(questionAnswer);
                } else if (question.getAnswerType().equals(AnswerType.CUSTOM)) {
                    String questionAnswer = questionAnswers.get(0);
                    isCorrect = correctAnswers.contains(questionAnswer);
                } else if (question.getAnswerType().equals(AnswerType.MULTIPLE)) {
                    int correctAnswersCount = 0;
                    for (String questionAnswer : questionAnswers) {
                        if (correctAnswers.contains(questionAnswer)) {
                            correctAnswersCount++;
                        } else {
                            isCorrect = false;
                        }
                    }
                    if (correctAnswersCount == correctAnswers.size())
                        mark += testQuestion.getValue();
                    else {
                        mark += testQuestion.getValue() *
                                ((correctAnswersCount + 0.0) / correctAnswers.size());
                    }
                }
                if (isCorrect && !question.getAnswerType().equals(AnswerType.MULTIPLE))
                    mark += testQuestion.getValue();
            }
            setStatistic(question, isCorrect);
        }

        if (mark > 100)
            mark = 100;
        return mark;
    }

    private void changeCoefficient(List<TestQuestion> testQuestions) {
        List<Question> questions = testQuestions.stream()
                .map(TestQuestion::getQuestion)
                .collect(Collectors.toList());
    }

    private void setStatistic(Question question, boolean correct) {
        QuestionStatistic questionStatistic = questionStatisticRepository.findByQuestion(question);
        if (questionStatistic == null) {
            questionStatistic = new QuestionStatistic();
            questionStatistic.setQuestion(question);
            questionStatistic.setCorrectAnswers(correct ? 1 : 0);
            questionStatistic.setWrongAnswers(correct ? 1 : 0);
        } else {
            int correctAnswers = questionStatistic.getCorrectAnswers();
            int wrongAnswers = questionStatistic.getWrongAnswers();
            questionStatistic.setCorrectAnswers(correctAnswers + (correct ? 1 : 0));
            questionStatistic.setWrongAnswers(wrongAnswers + (correct ? 1 : 0));
        }
        questionStatisticRepository.save(questionStatistic);
    }

    private TestAnswer getTestAnswer(Test test, User user, Question question, String answer) {
        TestAnswer testAnswer = new TestAnswer();
        testAnswer.setTest(test);
        testAnswer.setUser(user);
        testAnswer.setQuestion(question);
        testAnswer.setAnswer(answer);
        return testAnswer;
    }

    private TestAnswerDto getTestQuestionAnswerByUserAndNumber(Test test, User user, int number) {
        TestQuestion testQuestion = test.getQuestions().get(number - 1);
        Question question = testQuestion.getQuestion();

        TestAnswerDto dto = new TestAnswerDto();
        if (question.getAnswerType().equals(AnswerType.MATCH)) {
            List<Question> subQuestions = question.getSubQuestions();
            List<String> subQuestionsText = subQuestions.stream()
                    .map(Question::getText)
                    .collect(Collectors.toList());
            dto.setSubQuestionsText(subQuestionsText);

            List<TestAnswer> testAnswers = new ArrayList<>(subQuestions.size());
            for (Question subQuestion : subQuestions) {
                testAnswers.addAll(testAnswerRepository.findByTestAndUserAndQuestion(
                        test, user, subQuestion));
            }
            List<String> testAnswersText = testAnswers.stream()
                    .map(TestAnswer::getAnswer)
                    .collect(Collectors.toList());
            dto.setAnswers(testAnswersText);
        } else {
            List<TestAnswer> testAnswers = testAnswerRepository.findByTestAndUserAndQuestion(
                    test, user, question);
            if (testAnswers == null)
                dto.setAnswers(null);
            else {
                List<String> answers = testAnswers.stream()
                        .map(TestAnswer::getAnswer)
                        .collect(Collectors.toList());
                dto.setAnswers(answers);
            }
        }

        return dto;
    }

    private TestQuestionDto getTestQuestionByNumber(Test test, int number) {
        TestQuestion testQuestion = test.getQuestions().get(number - 1);
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
            Collections.shuffle(answersText);
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

        dto.setNextNumber(number + 1);
        dto.setPreviousNumber(number - 1);
        dto.setLast(number == test.getQuestionsCount());
        dto.setQuestionType(question.getType().getText());

        return dto;
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