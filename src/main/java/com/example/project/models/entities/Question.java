package com.example.project.models.entities;

import com.example.project.models.enums.AnswerType;
import com.example.project.models.enums.QuestionDifficulty;
import com.example.project.models.enums.QuestionType;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String text;
    @Column(name = "coef", precision = 4)
    private double coefficient;
    @Column(name = "media_url")
    private String mediaUrl;
    @Column(name = "answer_desc")
    private String answerDescription;
    @Enumerated(EnumType.STRING)
    @Column(length = 10)
    private QuestionType type;
    @Enumerated(EnumType.STRING)
    @Column(length = 6)
    private QuestionDifficulty difficulty;

    @Enumerated(EnumType.STRING)
    @Column(length = 8)
    private AnswerType answerType;

    @ManyToOne
    @JoinColumn(name = "topic_id", referencedColumnName = "id")
    private Topic topic;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "sup_question_id", referencedColumnName = "id")
    private Question supQuestion;

    @OneToMany(mappedBy = "question")
    private List<TestQuestion> inTests;

    @OneToMany(mappedBy = "supQuestion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> subQuestions;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Answer> answers;

    @OneToMany(mappedBy = "question")
    private List<TestAnswer> testAnswers;

    @OneToOne(mappedBy = "question")
    private QuestionStatistic statistic;

    public Question() {
    }

    public void setAnswer(Answer answer) {
        Answer originalAnswer = this.answers.stream()
                .filter(x -> Objects.equals(x.getId(), answer.getId()))
                .findFirst()
                .get();
        originalAnswer.setCorrect(answer.isCorrect());
        originalAnswer.setText(answer.getText());
    }

    public void setSubQuestion(Question question) {
        Question originalSubQuestion = this.subQuestions.stream()
                .filter(x -> Objects.equals(x.getId(), question.getId()))
                .findFirst()
                .get();
        originalSubQuestion.setText(question.getText());
        originalSubQuestion.setAnswer(question.getAnswers().get(0));
    }

    public void deleteAllAnswers() {
        this.answers.clear();
    }

    public void deleteAllSubQuestions() {
        this.subQuestions.clear();
    }

    public void addAnswer(Answer answer) {
        answer.setQuestion(this);
        this.answers.add(answer);
    }

    public void deleteAnswer(Answer answer) {
        this.answers.remove(answer);
    }

    public void addSubQuestion(Question question) {
        question.setSupQuestion(this);
        this.subQuestions.add(question);
    }

    public void deleteSubQuestion(Question question) {
        this.subQuestions.remove(question);
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(double coefficient) {
        this.coefficient = coefficient;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getAnswerDescription() {
        return answerDescription;
    }

    public void setAnswerDescription(String answerDescription) {
        this.answerDescription = answerDescription;
    }

    public QuestionType getType() {
        return type;
    }

    public void setType(QuestionType type) {
        this.type = type;
    }

    public QuestionDifficulty getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(QuestionDifficulty difficulty) {
        this.difficulty = difficulty;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Question getSupQuestion() {
        return supQuestion;
    }

    public void setSupQuestion(Question supQuestion) {
        this.supQuestion = supQuestion;
    }

    public List<TestQuestion> getInTests() {
        return inTests;
    }

    public void setInTests(List<TestQuestion> inTests) {
        this.inTests = inTests;
    }

    public List<Question> getSubQuestions() {
        return subQuestions;
    }

    public void setSubQuestions(List<Question> subQuestions) {
        this.subQuestions = subQuestions;
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Answer> answers) {
        this.answers = answers;
    }

    public QuestionStatistic getStatistic() {
        return statistic;
    }

    public void setStatistic(QuestionStatistic statistic) {
        this.statistic = statistic;
    }

    public Topic getTopic() {
        return topic;
    }

    public void setTopic(Topic topic) {
        this.topic = topic;
    }

    public AnswerType getAnswerType() {
        return answerType;
    }

    public void setAnswerType(AnswerType answerType) {
        this.answerType = answerType;
    }

    public List<TestAnswer> getTestAnswers() {
        return testAnswers;
    }

    public void setTestAnswers(List<TestAnswer> testAnswers) {
        this.testAnswers = testAnswers;
    }
}
