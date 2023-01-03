package com.example.project.models.entities;

import javax.persistence.*;

@Entity
@Table(name = "question_statistic")
public class QuestionStatistic {
    @Id
    @Column(name = "question_id")
    private Long id;
    @Column(name = "correct_answers")
    private int correctAnswers;
    @Column(name = "wrong_answers")
    private int wrongAnswers;
    @Column(name = "coef_change_answers_count")
    private int coefficientChangeAnswersCount;

    @OneToOne
    @MapsId
    @JoinColumn(name = "question_id")
    private Question question;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(int correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public int getWrongAnswers() {
        return wrongAnswers;
    }

    public void setWrongAnswers(int wrongAnswers) {
        this.wrongAnswers = wrongAnswers;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public int getCoefficientChangeAnswersCount() {
        return coefficientChangeAnswersCount;
    }

    public void setCoefficientChangeAnswersCount(int coefficientChangeAnswersCount) {
        this.coefficientChangeAnswersCount = coefficientChangeAnswersCount;
    }
}
