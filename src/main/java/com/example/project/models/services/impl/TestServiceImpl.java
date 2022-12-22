package com.example.project.models.services.impl;

import com.example.project.dto.page.PageDto;
import com.example.project.dto.test.*;
import com.example.project.models.entities.*;
import com.example.project.models.enums.AnswerType;
import com.example.project.models.enums.TestDifficulty;
import com.example.project.models.enums.TestType;
import com.example.project.models.mappers.AddTestMapper;
import com.example.project.models.repositories.*;
import com.example.project.models.services.TestService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class TestServiceImpl implements TestService {
    private final TestRepository testRepository;
    private final CurrentTestRepository currentTestRepository;
    private final FinishedTestRepository finishedTestRepository;
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;
    private final TestAnswerRepository testAnswerRepository;
    private final QuestionRepository questionRepository;

    public TestServiceImpl(TestRepository testRepository,
                           CurrentTestRepository currentTestRepository,
                           FinishedTestRepository finishedTestRepository,
                           UserRepository userRepository,
                           TopicRepository topicRepository,
                           TestAnswerRepository testAnswerRepository,
                           QuestionRepository questionRepository) {
        this.finishedTestRepository = finishedTestRepository;
        this.testRepository = testRepository;
        this.currentTestRepository = currentTestRepository;
        this.userRepository = userRepository;
        this.topicRepository = topicRepository;
        this.testAnswerRepository = testAnswerRepository;
        this.questionRepository = questionRepository;
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
        if (testWalkthroughDto.getAnswers() == null ||
                testWalkthroughDto.getAnswers().size() == 0)
            return;

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
            List<String> subQuestionText = testWalkthroughDto.getSubQuestionsText();
            List<String> answers = testWalkthroughDto.getAnswers();
            testAnswers = new ArrayList<>(answers.size());
            for (int i = 0; i < answers.size(); i++) {
                if (answers.get(i).equals(""))
                    continue;

                Question matchQuestion = questionRepository.findBySupQuestionAndText(question,
                        subQuestionText.get(i));
                List<TestAnswer> matchQuestionAnswer = testAnswerRepository.
                        findByTestAndUserAndQuestion(test, user, matchQuestion);

                TestAnswer testAnswer;
                if (matchQuestionAnswer == null || matchQuestionAnswer.size() == 0) {
                    testAnswer = getTestAnswer(test, user, matchQuestion,
                            answers.get(i));
                } else {
                    testAnswer = matchQuestionAnswer.get(0);
                    testAnswer.setAnswer(answers.get(i));
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

        if (finishedTestRepository.findByUserAndTest(user, test) != null)
            return;

        CurrentTest currentTest = currentTestRepository.findByUserAndTest(user, test);
        currentTestRepository.delete(currentTest);

        test.getUsersAssigned().remove(user);
        testRepository.save(test);

        double mark = getTestMark(test, user);
        FinishedTest finishedTest = new FinishedTest();
        finishedTest.setTest(test);
        finishedTest.setUser(user);
        finishedTest.setMark(mark);

        finishedTestRepository.save(finishedTest);
    }

    public PageDto<Test> getPage(String type, String name, User user, int page, int limit) {
        TestType testType = TestType.ASSIGNED;
        String username = user.getUsername();
        List<Test> tests;
        if (type != null)
            testType = TestType.getByText(type);
        switch (testType) {
            case ASSIGNED:
                tests = getAssignedToUserByUsername(username);
                break;
            case FINISHED:
                tests = getFinishedByUser(user);
                break;
            case CURRENT:
                tests = getCurrentOfUserByUser(user);
                break;
            case CREATED:
                tests = getCreatedByUser(user);
                break;
            default:
                throw new IllegalArgumentException();
        }
        if (name != null && !name.equals("")) {
            tests = tests.stream()
                    .filter(x -> x.getName().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());
        }
        return getPageByList(tests, page, limit);
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

        Duration timePassed = Duration.between(dateStarted, now);
        Duration timeLeft = Duration.between(LocalTime.of(0, 0, 0), timeLimit)
                .minus(timePassed);
        if (timeLeft.isNegative() || timeLeft.isZero()) {
            return LocalTime.of(0, 0, 0);
        } else {
            return LocalTime.of(0, 0, 0).plusNanos(timeLeft.toNanos());
        }
    }

    @Override
    public boolean isTestOutdated(String testId) {
        Test test = testRepository.findById(testId).orElseThrow(NoSuchElementException::new);
        LocalDateTime testFinishDate = test.getFinishDate();

        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(testFinishDate);
    }

    @Override
    public boolean isTimeUnlimited(String testId) {
        Test test = testRepository.findById(testId).orElseThrow(NoSuchElementException::new);
        LocalTime testTimeLimit = test.getTimeLimit();
        return testTimeLimit.toSecondOfDay() == 0;
    }

    @Override
    public boolean isTestTooEarly(String testId) {
        Test test = testRepository.findById(testId).orElseThrow(NoSuchElementException::new);
        LocalDateTime testStartDate = test.getStartDate();

        LocalDateTime now = LocalDateTime.now();
        return now.isBefore(testStartDate);
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
    public boolean isUserCreated(String testId, User user) {
        Test test = testRepository.findById(testId)
                .orElseThrow(NoSuchElementException::new);
        return test.getUserCreated().getUsername().equals(user.getUsername());
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
    public TestDto getIntro(Test test) {
        TestDto dto = new TestDto();
        dto.setName(test.getName());
        dto.setDifficulty(test.getDifficulty().getText());
        dto.setAuthorUsername(test.getUserCreated().getUsername());
        dto.setQuestionsCount(test.getQuestionsCount());
        dto.setTimeLimit(test.getTimeLimit());
        dto.setTopics(getTestTopics(test).stream()
                .map(Topic::getName)
                .collect(Collectors.toList()));
        dto.setDateFinish(test.getFinishDate());
        dto.setDateStarted(test.getStartDate());
        return dto;
    }

    @Override
    public TestResultInfoDto getResultInfo(String testId) {
        Test test = testRepository.findById(testId)
                .orElseThrow(NoSuchElementException::new);
        TestResultInfoDto dto = new TestResultInfoDto();
        dto.setName(test.getName());
        dto.setTimeLimit(test.getTimeLimit());
        dto.setQuestionsCount(test.getQuestionsCount());
        dto.setTopics(test.getTopics().stream()
                .map(Topic::getName)
                .collect(Collectors.toList()));
        dto.setDifficulty(test.getDifficulty().getText());
        dto.setDateStarted(test.getStartDate());
        dto.setDateFinish(test.getFinishDate());

        Integer usersAssignedCount = test.getUsersAssigned().size();
        List<FinishedTest> finishedTests = test.getFinishedTests();
        Integer userCompletedCount = finishedTests.size();
        List<String> userCompletedUsernames = finishedTests.stream()
                .map(x -> x.getUser().getUsername())
                .collect(Collectors.toList());

        dto.setUserAssignedCount(usersAssignedCount);
        dto.setUserCompletedCount(userCompletedCount);
        dto.setUserCompletedUsernames(userCompletedUsernames);
        return dto;
    }

    @Override
    public TestResultDto getUserTestResult(String testId, User user) {
        Test test = testRepository.findById(testId)
                .orElseThrow(NoSuchElementException::new);
        FinishedTest finishedTest = finishedTestRepository.findByUserAndTest(user, test);

        TestResultDto dto = new TestResultDto();
        dto.setTestName(test.getName());
        dto.setUsername(user.getUsername());
        dto.setMark(finishedTest.getMark());

        List<TestQuestionDto> questions = new ArrayList<>(test.getQuestionsCount());
        List<TestAnswerDto> answers = new ArrayList<>(test.getQuestionsCount());

        for (int i = 0; i < test.getQuestionsCount(); i++) {
            TestQuestionDto question = getTestQuestionByNumber(test, i + 1);
            TestAnswerDto answer = getTestQuestionAnswerByUserAndNumber(test, user, i + 1);

            questions.add(question);
            answers.add(answer);
        }

        dto.setQuestions(questions);
        dto.setAnswers(answers);

        return dto;
    }

    @Override
    public Set<Topic> getTestTopics(Test test) {
        return test.getTopics();
    }

    @Override
    public List<String> getTestDifficulties() {
        return TestDifficulty.getValuesText();
    }

    @Override
    public List<String> getTestTypes() {
        return TestType.getValuesText();
    }

    private double getTestMark(Test test, User user) {
        double mark = 0.0;
        List<TestQuestion> testQuestions = test.getQuestions();

        for (TestQuestion testQuestion : testQuestions) {
            Question question = testQuestion.getQuestion();
            boolean isCorrect = true;
            if (question.getAnswerType().equals(AnswerType.MATCH)) {
                int correctAnswersCount = 0;
                List<Question> subQuestions = questionRepository.findAllBySupQuestion(question);
                List<TestAnswer> submitTestAnswers = new ArrayList<>(subQuestions.size());
                for (Question subQuestion : subQuestions) {
                    List<TestAnswer> testAnswers = testAnswerRepository.findByTestAndUserAndQuestion(
                            test, user, subQuestion);
                    if (testAnswers == null || testAnswers.size() == 0)
                        continue;
                    TestAnswer testAnswer = testAnswers.get(0);

                    String correctAnswer = subQuestion
                            .getAnswers().get(0).getText();
                    if (correctAnswer.equals(testAnswer.getAnswer())) {
                        testAnswer.setCorrect(true);
                        correctAnswersCount++;
                    } else {
                        testAnswer.setCorrect(false);
                    }
                    submitTestAnswers.add(testAnswer);
                }
                if (correctAnswersCount == subQuestions.size())
                    mark += testQuestion.getValue();
                else {
                    mark += testQuestion.getValue() *
                            ((correctAnswersCount + 0.0) / subQuestions.size());
                }
                testAnswerRepository.saveAll(submitTestAnswers);
            } else {
                List<TestAnswer> testAnswers = testAnswerRepository.findByTestAndUserAndQuestion(
                        test, user, question);
                if (testAnswers == null || testAnswers.size() == 0)
                    continue;
                List<String> correctAnswers = question.getAnswers().stream()
                        .filter(Answer::isCorrect)
                        .map(Answer::getText)
                        .collect(Collectors.toList());
                List<String> questionAnswers = testAnswers.stream()
                        .map(TestAnswer::getAnswer)
                        .collect(Collectors.toList());
                if (question.getAnswerType().equals(AnswerType.SINGLE)) {
                    String correctAnswer = correctAnswers.get(0);
                    String questionAnswer = questionAnswers.get(0);
                    isCorrect = correctAnswer.equals(questionAnswer);
                    testAnswers.get(0).setCorrect(isCorrect);
                } else if (question.getAnswerType().equals(AnswerType.CUSTOM)) {
                    String questionAnswer = questionAnswers.get(0);
                    isCorrect = correctAnswers.contains(questionAnswer);
                    testAnswers.get(0).setCorrect(isCorrect);
                } else if (question.getAnswerType().equals(AnswerType.MULTIPLE)) {
                    int correctAnswersCount = 0;

                    for (int i = 0; i < testAnswers.size(); i++) {
                        if (correctAnswers.contains(questionAnswers.get(i))) {
                            testAnswers.get(i).setCorrect(true);
                            correctAnswersCount++;
                        } else {
                            isCorrect = false;
                            testAnswers.get(i).setCorrect(false);
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

                testAnswerRepository.saveAll(testAnswers);
            }
        }

        if (mark > 99.99)
            mark = 100;
        return mark;
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
            List<Question> subQuestions = questionRepository.findAllBySupQuestion(question);
            List<String> subQuestionsText = subQuestions.stream()
                    .map(Question::getText)
                    .collect(Collectors.toList());
            dto.setSubQuestionsText(subQuestionsText);

            List<TestAnswer> testAnswers = new ArrayList<>(subQuestions.size());

            List<TestAnswer> firstSubQuestionAnswers = testAnswerRepository
                    .findByTestAndUserAndQuestion(test, user, subQuestions.get(0));
            boolean isAnswersSet = true;
            if (firstSubQuestionAnswers == null || firstSubQuestionAnswers.size() == 0) {
                isAnswersSet = false;
                dto.setMatchQuestionNumOfAnswers(null);
            } else {
                for (Question subQuestion : subQuestions) {
                    testAnswers.addAll(testAnswerRepository.findByTestAndUserAndQuestion(
                            test, user, subQuestion));
                }
            }

            List<Answer> answers = subQuestions.stream()
                    .map(x -> x.getAnswers().get(0))
                    .collect(Collectors.toList());
            Collections.shuffle(answers);
            List<String> testAnswersText = answers.stream()
                    .map(Answer::getText)
                    .collect(Collectors.toList());
            dto.setAnswers(testAnswersText);

            if (isAnswersSet) {
                List<Integer> numOfAnswers = new ArrayList<>(subQuestionsText.size());
                for (int i = 0; i < subQuestionsText.size(); i++) {
                    numOfAnswers.add(testAnswersText.indexOf(testAnswers.get(i).getAnswer()));
                }
                dto.setMatchQuestionNumOfAnswers(numOfAnswers);
            }
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

        if (question.getAnswerType().equals(AnswerType.SINGLE) ||
                question.getAnswerType().equals(AnswerType.MULTIPLE)) {
            List<Answer> answers = question.getAnswers();
            List<String> answersText = answers.stream()
                    .map(Answer::getText)
                    .collect(Collectors.toList());
            dto.setAnswersVariants(answersText);
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

    private List<Test> getAssignedToUserByUsername(String username) {
        return testRepository.findTestsByUsersAssignedUsername(username);
    }

    private List<Test> getFinishedByUser(User user) {
        List<FinishedTest> finishedTests = finishedTestRepository.findFinishedTestsByUser(user);
        return finishedTests.stream()
                .map(FinishedTest::getTest)
                .collect(Collectors.toList());
    }

    private List<Test> getCurrentOfUserByUser(User user) {
        List<CurrentTest> currentTests = currentTestRepository.findCurrentTestsByUser(user);
        return currentTests.stream()
                .map(CurrentTest::getTest)
                .collect(Collectors.toList());
    }

    private List<Test> getCreatedByUser(User user) {
        return testRepository.findAllByUserCreated(user);
    }

    private PageDto<Test> getPageByList(List<Test> tests, int page, int limit) {
        tests = tests.stream()
                .skip((long) (page - 1) * limit)
                .limit(limit)
                .collect(Collectors.toList());
        int totalPages = tests.size() / limit;
        totalPages += tests.size() % limit > 0 ? 1 : 0;
        return new PageDto<>(tests, page, totalPages);
    }
}