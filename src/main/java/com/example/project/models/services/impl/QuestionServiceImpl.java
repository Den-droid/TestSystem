package com.example.project.models.services.impl;

import com.example.project.dto.page.PageDto;
import com.example.project.dto.question.AddQuestionDto;
import com.example.project.dto.question.AddQuestionPageDto;
import com.example.project.dto.question.EditQuestionDto;
import com.example.project.dto.question.EditQuestionPageDto;
import com.example.project.models.entities.*;
import com.example.project.models.enums.AnswerType;
import com.example.project.models.enums.QuestionDifficulty;
import com.example.project.models.enums.QuestionType;
import com.example.project.models.mappers.AddQuestionMapper;
import com.example.project.models.mappers.EditQuestionMapper;
import com.example.project.models.repositories.*;
import com.example.project.models.services.QuestionService;
import com.example.project.models.utils.FileUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class QuestionServiceImpl implements QuestionService {
    private final QuestionRepository questionRepository;
    private final TopicRepository topicRepository;
    private final AnswerRepository answerRepository;
    private final TestQuestionRepository testQuestionRepository;
    private final QuestionStatisticRepository questionStatisticRepository;
    private final TestRepository testRepository;
    private final TestAnswerRepository testAnswerRepository;

    public QuestionServiceImpl(QuestionRepository questionRepository,
                               TopicRepository topicRepository,
                               AnswerRepository answerRepository,
                               TestQuestionRepository testQuestionRepository,
                               QuestionStatisticRepository questionStatisticRepository,
                               TestRepository testRepository,
                               TestAnswerRepository testAnswerRepository) {
        this.questionRepository = questionRepository;
        this.topicRepository = topicRepository;
        this.answerRepository = answerRepository;
        this.testQuestionRepository = testQuestionRepository;
        this.questionStatisticRepository = questionStatisticRepository;
        this.testRepository = testRepository;
        this.testAnswerRepository = testAnswerRepository;
    }

    @Override
    public void add(int topicId, User user, AddQuestionDto dto, MultipartFile file) throws IOException {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(NoSuchElementException::new);

        Question question = AddQuestionMapper.map(dto);
        if (file != null && !file.isEmpty()) {
            String mediaName = FileUtils.save(file);
            question.setMediaUrl(mediaName);
        }

        question.setTopic(topic);
        question.setUser(user);

        questionRepository.save(question);
    }

    @Override
    public void edit(long questionId, EditQuestionDto dto, MultipartFile file) throws IOException {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(NoSuchElementException::new);

        Question editQuestion = EditQuestionMapper.map(dto);

        if (question.getAnswerType() != editQuestion.getAnswerType()) {
            if (question.getAnswerType() == AnswerType.MATCH) {
                question.deleteAllSubQuestions();
            } else {
                question.deleteAllAnswers();
            }

            if (editQuestion.getAnswerType() == AnswerType.MATCH) {
                for (Question subQuestion : editQuestion.getSubQuestions()) {
                    question.addSubQuestion(subQuestion);
                }
            } else {
                for (Answer answer : editQuestion.getAnswers()) {
                    question.addAnswer(answer);
                }
            }
        } else {
            if (question.getAnswerType() == AnswerType.MATCH) {
                List<Long> notDeletedIds = editQuestion.getSubQuestions().stream()
                        .map(Question::getId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                List<Question> deletedSubQuestions = questionRepository.findByIdNotInAndSupQuestion(
                        notDeletedIds, question
                );

                for (Question subQuestion : deletedSubQuestions) {
                    question.deleteSubQuestion(subQuestion);
                }
            } else {
                List<Long> notDeletedIds = editQuestion.getAnswers().stream()
                        .map(Answer::getId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                List<Answer> deletedAnswers = answerRepository.findAnswersByIdNotInAndQuestion(
                        notDeletedIds, question
                );

                for (Answer answer : deletedAnswers) {
                    question.deleteAnswer(answer);
                }
            }

            if (editQuestion.getAnswerType() == AnswerType.MATCH) {
                List<Question> addedSubQuestions = editQuestion.getSubQuestions().stream()
                        .filter(x -> x.getId() == null)
                        .collect(Collectors.toList());

                List<Question> existedSubQuestions = editQuestion.getSubQuestions().stream()
                        .filter(x -> x.getId() != null)
                        .collect(Collectors.toList());

                for (Question existedSubQuestion : existedSubQuestions) {
                    question.setSubQuestion(existedSubQuestion);
                }

                for (Question subQuestion : addedSubQuestions) {
                    question.addSubQuestion(subQuestion);
                }
            } else {
                List<Answer> addedAnswers = editQuestion.getAnswers().stream()
                        .filter(x -> x.getId() == null)
                        .collect(Collectors.toList());

                List<Answer> existedAnswers = editQuestion.getAnswers().stream()
                        .filter(x -> x.getId() != null)
                        .collect(Collectors.toList());

                for (Answer existedAnswer : existedAnswers) {
                    question.setAnswer(existedAnswer);
                }

                for (Answer answer : addedAnswers) {
                    question.addAnswer(answer);
                }
            }
        }

        if (file != null && !file.isEmpty()) {
            if (question.getMediaUrl() != null)
                FileUtils.delete(question.getMediaUrl());
            String mediaName = FileUtils.save(file);
            question.setMediaUrl(mediaName);
        } else {
            if (editQuestion.getType() == QuestionType.TEXT &&
                    question.getType() != QuestionType.TEXT)
                FileUtils.delete(question.getMediaUrl());
        }

        question.setType(editQuestion.getType());
        question.setDifficulty(editQuestion.getDifficulty());
        question.setAnswerType(editQuestion.getAnswerType());
        question.setText(editQuestion.getText());
        question.setAnswerDescription(editQuestion.getAnswerDescription());
        questionRepository.save(question);
    }

    @Override
    public void delete(long id) throws IOException {
        Question question = questionRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);
        if (canBeChanged(question)) {
            if (question.getMediaUrl() != null)
                FileUtils.delete(question.getMediaUrl());

            questionRepository.delete(question);
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    public Question getById(long id) {
        return questionRepository.findById(id).orElseThrow(NoSuchElementException::new);
    }

    @Override
    public EditQuestionPageDto getEditQuestionPage(Question question) {
        EditQuestionPageDto dto = new EditQuestionPageDto();

        dto.setQuestionDifficulties(getQuestionDifficulties());
        dto.setQuestionTypes(getQuestionTypes());
        dto.setAnswerTypes(getAnswerTypes());
        if (question.getAnswerType().equals(AnswerType.MATCH)) {
            dto.setSubQuestions(getSubQuestions(question));
            dto.setAnswers(getSubQuestionAnswers(question));
        } else {
            dto.setAnswers(getAnswers(question));
        }

        return dto;
    }

    @Override
    public AddQuestionPageDto getAddQuestionPage() {
        AddQuestionPageDto dto = new AddQuestionPageDto();
        dto.setQuestionDifficulties(getQuestionDifficulties());
        dto.setQuestionTypes(getQuestionTypes());
        dto.setAnswerTypes(getAnswerTypes());
        return dto;
    }

    @Override
    public boolean canBeChanged(Question question) {
        List<TestQuestion> tests = testQuestionRepository.findByQuestion(question);
        return tests == null || tests.size() == 0;
    }

    @Override
    public List<QuestionStatistic> getStatistic(List<Question> questions) {
        List<QuestionStatistic> questionStatistics = new ArrayList<>(questions.size());
        for (Question question : questions) {
            QuestionStatistic questionStatistic = questionStatisticRepository.findByQuestion(question);
            questionStatistics.add(questionStatistic);
        }
        return questionStatistics;
    }

    @Override
    public void setStatistic(String testId, User user) {
        Test test = testRepository.findById(testId).orElseThrow(NoSuchElementException::new);
        List<TestAnswer> testAnswers = testAnswerRepository.findByTestAndUser(test, user);
        List<TestQuestion> testQuestions = testQuestionRepository.findAllByTest(test);
        List<QuestionStatistic> statistics = new ArrayList<>(testQuestions.size());
        for (TestQuestion testQuestion : testQuestions) {
            Question question = testQuestion.getQuestion();
            boolean isCorrect;
            if (question.getAnswerType().equals(AnswerType.MATCH)) {
                List<TestAnswer> subQuestionsAnswers = testAnswers.stream()
                        .filter(x -> {
                            Question subQuestion = x.getQuestion().getSupQuestion();
                            return subQuestion != null && subQuestion.equals(question);
                        })
                        .collect(Collectors.toList());
                isCorrect = subQuestionsAnswers.stream()
                        .allMatch(TestAnswer::isCorrect);
            } else if (question.getAnswerType().equals(AnswerType.MULTIPLE)) {
                isCorrect = testAnswers.stream()
                        .filter(x -> x.getQuestion().equals(question))
                        .allMatch(TestAnswer::isCorrect);
            } else {
                isCorrect = testAnswers.stream()
                        .filter(x -> x.getQuestion().equals(question))
                        .anyMatch(TestAnswer::isCorrect);
            }
            QuestionStatistic questionStatistic = setStatistic(question, isCorrect);
            statistics.add(questionStatistic);
        }
        questionStatisticRepository.saveAll(statistics);
    }

    @Override
    public void changeCoefficient(String testId) {
        Test test = testRepository.findById(testId).orElseThrow(NoSuchElementException::new);
        List<Question> questions = test.getQuestions().stream()
                .map(TestQuestion::getQuestion)
                .collect(Collectors.toList());
        changeCoefficient(questions);
        questionRepository.saveAll(questions);
    }

    @Override
    public PageDto<Question> getPageByTopic(int topicId, int page, int limit) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(NoSuchElementException::new);
        Page<Question> questionPage = questionRepository.findAllByTopic(topic,
                PageRequest.of(page - 1, limit));
        return new PageDto<>(questionPage.getContent(), page, questionPage.getTotalPages());
    }

    @Override
    public PageDto<Question> getPageByTopicAndName(String text, int topicId, int page, int limit) {
        Topic topic = topicRepository.findById(topicId)
                .orElseThrow(NoSuchElementException::new);
        Page<Question> questionPage = questionRepository
                .findAllByTopicAndTextContainsIgnoreCase(topic, text,
                        PageRequest.of(page - 1, limit));
        return new PageDto<>(questionPage.getContent(), page, questionPage.getTotalPages());
    }

    @Override
    public PageDto<Question> getPageByUser(User user, int page, int limit) {
        Page<Question> questionPage = questionRepository.findAllByUser(user,
                PageRequest.of(page - 1, limit));
        return new PageDto<>(questionPage.getContent(), page, questionPage.getTotalPages());
    }

    @Override
    public PageDto<Question> getPageByUserAndName(User user, String text, int page, int limit) {
        Page<Question> questionPage = questionRepository.findAllByUserAndTextContainsIgnoreCase(user, text,
                PageRequest.of(page - 1, limit));
        return new PageDto<>(questionPage.getContent(), page, questionPage.getTotalPages());
    }

    private List<String> getQuestionTypes() {
        return QuestionType.getValuesText();
    }

    private List<String> getQuestionDifficulties() {
        return QuestionDifficulty.getValuesText();
    }

    private List<String> getAnswerTypes() {
        return AnswerType.getValuesText();
    }

    private List<Answer> getAnswers(Question question) {
        return question.getAnswers();
    }

    private List<Question> getSubQuestions(Question question) {
        return question.getSubQuestions();
    }

    private List<Answer> getSubQuestionAnswers(Question question) {
        return question.getSubQuestions().stream()
                .map(Question::getAnswers)
                .collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);
    }

    private void changeCoefficient(List<Question> questions) {

    }

    private QuestionStatistic setStatistic(Question question, boolean correct) {
        QuestionStatistic questionStatistic = questionStatisticRepository.findByQuestion(question);
        if (questionStatistic == null) {
            questionStatistic = new QuestionStatistic();
            questionStatistic.setQuestion(question);
            questionStatistic.setCorrectAnswers(correct ? 1 : 0);
            questionStatistic.setWrongAnswers(correct ? 0 : 1);
        } else {
            int correctAnswers = questionStatistic.getCorrectAnswers();
            int wrongAnswers = questionStatistic.getWrongAnswers();
            questionStatistic.setCorrectAnswers(correctAnswers + (correct ? 1 : 0));
            questionStatistic.setWrongAnswers(wrongAnswers + (correct ? 0 : 1));
        }
        return questionStatistic;
    }
}
