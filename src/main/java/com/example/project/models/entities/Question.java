package com.example.project.models.entities;

import com.example.project.models.enums.QuestionDifficulty;
import com.example.project.models.enums.QuestionType;

import javax.persistence.*;
import java.util.List;

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

    @OneToMany(mappedBy = "supQuestion")
    private List<Question> subQuestions;

    @OneToMany(mappedBy = "question")
    private List<Answer> answers;

    @OneToOne(mappedBy = "question")
    private QuestionStatistic statistic;

    public Question() {
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
}
